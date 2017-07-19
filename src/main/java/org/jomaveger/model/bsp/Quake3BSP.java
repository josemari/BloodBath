package org.jomaveger.model.bsp;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBMultitexture.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.log4j.Logger;
import org.jomaveger.core.GameCore;
import org.jomaveger.input.Camera;

import org.jomaveger.math.Vector;
import org.jomaveger.texture.Texture;
import org.jomaveger.texture.TextureCoord;
import org.jomaveger.texture.TextureManager;
import org.jomaveger.util.BinaryLoader;
import org.jomaveger.util.BufferUtil;

/**
 * @author jmvegas.gertrudix
 */
public class Quake3BSP {
    
    private static final Logger LOGGER = Logger.getLogger(Quake3BSP.class);

    public final static int FACE_POLYGON = 1;
    private static final float EPSILON = 0.03125f;		// This is our small number to compensate for float errors

    private static final int TYPE_RAY = 0;				// This is the type for tracing a RAY
    private static final int TYPE_SPHERE = 1;				// This is the type for tracing a SPHERE
    private static final int TYPE_BOX = 2;

    public static int visibleFaces;

    private int numOfVerts;			// The number of verts in the model
    private int numOfFaces;			// The number of faces in the model
    private int numOfIndices;		// The number of indices for the model
    private int numOfTextures;		// The number of texture maps
    private int numOfLightmaps;
    private int numOfNodes;			// The number of nodes in the level
    private int numOfLeafs;			// The leaf count in the level
    private int numOfLeafFaces;		// The number of leaf faces in the level
    private int numOfPlanes;			// The number of planes in the level
    private int numOfBrushes;			// The number of brushes in our world
    private int numOfBrushSides;		// The number of brush sides in our world
    private int numOfLeafBrushes;		// The number of leaf brushes

    private int traceType;			// This stores if we are checking a ray, sphere or a box
    private float traceRatio;			// This stores the ratio from our start pos to the intersection pt.
    private float traceRadius;		// This stores the sphere's radius for a collision offset

    private boolean m_bCollided;			// This tells if we just m_bCollided or not

    private int[] indices;	// The object's indices for rendering
    private BSPVertex[] verts;		// The object's vertices
    private BSPFace[] faces;		// The faces information of the object
    private BSPNode[] nodes;		// The nodes in the bsp tree
    private BSPLeaf[] leafs;		// The leafs in the bsp tree
    private BSPPlane[] planes;		// The planes stored in the bsp tree
    private int[] leafFaces;	// The leaf's faces in the bsp tree
    private BSPVisData clusters;	// The clusters in the bsp tree for space partitioning
    private BSPTexture[] textures;		// This stores our texture info for each brush
    private BSPBrush[] brushes;		// This is our brushes
    private BSPBrushSide[] brushSides;	// This holds the brush sides
    private int[] leafBrushes;  // The indices into the brush array

    private BitSet facesDrawn;		// The bitset for the faces that have/haven't been drawn

    private BinaryLoader loader;
    private final TextureManager texManager;
    private final TextureManager texManagerLight;

    IntBuffer lightBuffer;

    private Texture texLight;

    private boolean hasTextures;
    private boolean hasLightmaps;
    private Vector collisionNormal;
    private Vector extents;
    private Vector traceMaxs;
    private Vector traceMins;

    public Quake3BSP() {
        // Here we simply initialize our member variables to 0
        numOfVerts = 0;
        numOfFaces = 0;
        numOfIndices = 0;
        numOfTextures = 0;
        setNumOfLightmaps(0);

        verts = null;
        faces = null;
        indices = null;

        texManager = new TextureManager();
        texManagerLight = new TextureManager();

        lightBuffer = ByteBuffer.allocateDirect(4 * 100).order(ByteOrder.nativeOrder()).asIntBuffer();

        setHasTextures(true);
        setHasLightmaps(true);
        
        // We need to initialize our Min and Max and Extent variables
	traceMins = new Vector(0, 0, 0);
	traceMaxs = new Vector(0, 0, 0);
	extents   = new Vector(0, 0, 0);
	
	// This will store the normal of the plane we m_bCollided with
        collisionNormal = new Vector(0, 0, 0);
    }

    // This is our integer vector structure
    public class Vector3i {

        int x;
        int y;
        int z;				// The x y and z position of our integer vector

        public Vector3i() {
            x = loader.readInt();
            y = loader.readInt();
            z = loader.readInt();
        }
    }

    // This is our BSP header structure
    public class BSPHeader {

        String nameID;				// This should always be 'IBSP'
        int version;				// This should be 0x2e for Quake 3 files

        public BSPHeader() {
            nameID = loader.readString(4);
            version = loader.readInt();
        }
    }

    // This is our BSP lump structure
    public class BSPLump {

        int offset;					// The offset into the file for the start of this lump
        int length;					// The length in bytes for this lump

        public BSPLump() {
            offset = loader.readInt();
            length = loader.readInt();
        }
    }

    // This is our BSP lightmap structure which stores the 128x128 RGB values
    public class BSPLightMap {

        byte[] imageBits = new byte[128 * 128 * 3];   // The RGB data in a 128x128 image

        public BSPLightMap() {
            int count = 0;
            for (int i = 0; i < 128; i++) {
                for (int j = 0; j < 128; j++) {
                    for (int k = 0; k < 3; k++) {
                        imageBits[count] = (byte) loader.readByte();
                        count++;
                    }
                }
            }
        }
    }

    // This is our BSP vertex structure
    public class BSPVertex {

        Vector position;				// (x, y, z) position. 
        TextureCoord textureCoord;		// (u, v) texture coordinate
        TextureCoord lightmapCoord;		// (u, v) lightmap coordinate
        Vector normal;				// (x, y, z) normal vector
        byte[] color = new byte[4];		// RGBA color for the vertex 

        public BSPVertex() {
            position = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            textureCoord = new TextureCoord(loader.readFloat(), loader.readFloat());
            lightmapCoord = new TextureCoord(loader.readFloat(), loader.readFloat());
            normal = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());

            for (int i = 0; i < color.length; i++) {
                color[i] = (byte) loader.readByte();
            }
        }
    }

    // This is our BSP face structure
    public class BSPFace {

        int textureID;				// The index into the texture array 
        int effect;					// The index for the effects (or -1 = n/a) 
        int type;					// 1=polygon, 2=patch, 3=mesh, 4=billboard 
        int startVertIndex;			// The starting index into this face's first vertex 
        int numOfVerts;				// The number of vertices for this face 
        int startIndex;				// The starting index into the indices array for this face
        int numOfIndices;			// The number of indices for this face
        int lightmapID;				// The texture index for the lightmap 
        int[] lMapCorner = new int[2];			// The face's lightmap corner in the image 
        int[] lMapSize = new int[2];			// The size of the lightmap section 
        Vector lMapPos;			// The 3D origin of lightmap. 
        Vector[] lMapVecs = new Vector[2];		// The 3D space for s and t unit vectors. 
        Vector normal;			// The face normal. 
        int[] size = new int[2];				// The bezier patch dimensions. 

        FloatBuffer vertFloatBuffer;
        FloatBuffer texFloatBuffer;
        FloatBuffer lightFloatBuffer;

        IntBuffer indiceIntBuffer;

        public BSPFace() {
            textureID = loader.readInt();
            effect = loader.readInt();
            type = loader.readInt();
            startVertIndex = loader.readInt();
            numOfVerts = loader.readInt();
            startIndex = loader.readInt();
            numOfIndices = loader.readInt();
            lightmapID = loader.readInt();
            lMapCorner[0] = loader.readInt();
            lMapCorner[1] = loader.readInt();
            lMapSize[0] = loader.readInt();
            lMapSize[1] = loader.readInt();
            lMapPos = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            lMapVecs[0] = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            lMapVecs[1] = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            normal = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            size[0] = loader.readInt();
            size[1] = loader.readInt();

            vertFloatBuffer = getVertFloatBuffer();
            texFloatBuffer = getTexFloatBuffer();
            lightFloatBuffer = getLightFloatBuffer();
            indiceIntBuffer = getIndiceIntBuffer();

        }

        private FloatBuffer getVertFloatBuffer() {
            int b = 0;
            float[] tempVertFloat = new float[numOfVerts * 3];
            for (int a = startVertIndex; a < startVertIndex + numOfVerts; a++) {
                tempVertFloat[b] = verts[a].position.x;
                tempVertFloat[b + 1] = verts[a].position.y;
                tempVertFloat[b + 2] = verts[a].position.z;
                b += 3;

            }

            return BufferUtil.INSTANCE.AllocFloats(tempVertFloat);
        }

        private FloatBuffer getTexFloatBuffer() {
            int c = 0;
            float[] tempTexFloat = new float[numOfVerts * 2];
            for (int a = startVertIndex; a < startVertIndex + numOfVerts; a++) {
                tempTexFloat[c] = verts[a].textureCoord.s;
                tempTexFloat[c + 1] = verts[a].textureCoord.t;
                c += 2;

            }

            return BufferUtil.INSTANCE.AllocFloats(tempTexFloat);
        }

        private FloatBuffer getLightFloatBuffer() {
            float[] tempLightFloat = new float[numOfVerts * 2];
            int c = 0;
            for (int a = startVertIndex; a < startVertIndex + numOfVerts; a++) {
                tempLightFloat[c] = verts[a].lightmapCoord.s;
                tempLightFloat[c + 1] = verts[a].lightmapCoord.t;
                c += 2;

            }
            return BufferUtil.INSTANCE.AllocFloats(tempLightFloat);
        }

        private IntBuffer getIndiceIntBuffer() {
            int[] tempIndicesInt = new int[numOfIndices];

            for (int a = 0; a < tempIndicesInt.length; a++) {
                tempIndicesInt[a] = indices[a + startIndex];
            }

            return BufferUtil.INSTANCE.AllocInts(tempIndicesInt);
        }
    }

    // This is our BSP texture structure
    public class BSPTexture {

        String textureName;				// The name of the texture w/o the extension 
        int flags;					// The surface flags (unknown) 
        int textureType;				// The content flags (unknown)

        public BSPTexture() {
            textureName = loader.readString(64);
            flags = loader.readInt();
            textureType = loader.readInt();
        }
    }

    // This stores a node in the BSP tree
    public class BSPNode {

        int plane;					// The index into the planes array 
        int front;					// The child index for the front node 
        int back;					// The child index for the back node 
        Vector3i min;				// The bounding box min position. 
        Vector3i max;				// The bounding box max position. 

        public BSPNode() {
            plane = loader.readInt();
            front = loader.readInt();
            back = loader.readInt();
            min = new Vector3i();
            max = new Vector3i();
        }
    }

    // This stores a leaf (end node) in the BSP tree
    public class BSPLeaf {

        int cluster;				// The visibility cluster 
        int area;					// The area portal 
        Vector3i min;				// The bounding box min position 
        Vector3i max;				// The bounding box max position 
        int leafFace;				// The first index into the face array 
        int numOfLeafFaces;			// The number of faces for this leaf 
        int leafBrush;				// The first index for into the brushes 
        int numOfLeafBrushes;		// The number of brushes for this leaf

        public BSPLeaf() {
            cluster = loader.readInt();
            area = loader.readInt();
            min = new Vector3i();
            max = new Vector3i();
            leafFace = loader.readInt();
            numOfLeafFaces = loader.readInt();
            leafBrush = loader.readInt();
            numOfLeafBrushes = loader.readInt();
        }
    }

    // This stores a splitter plane in the BSP tree
    public class BSPPlane {

        Vector normal;			// Plane normal. 
        float distance;				// The plane distance from origin

        public BSPPlane() {
            normal = new Vector(loader.readFloat(), loader.readFloat(), loader.readFloat());
            distance = loader.readFloat();
        }
    }

    // This stores the cluster data for the PVS's
    public class BSPVisData {

        int numOfClusters;			// The number of clusters
        int bytesPerCluster;		// The amount of bytes (8 bits) in the cluster's bitset
        byte[] bitSets = null;			// The array of bytes that holds the cluster bitsets

        public BSPVisData() {
            numOfClusters = loader.readInt();
            bytesPerCluster = loader.readInt();

            int size = numOfClusters * bytesPerCluster;
            bitSets = new byte[size];
            for (int i = 0; i < bitSets.length; i++) {
                bitSets[i] = (byte) loader.readByte();
            }
        }
    }

    // This stores the brush data
    public class BSPBrush {

        int brushSide;				// The starting brush side for the brush 
        int numOfBrushSides;		// Number of brush sides for the brush
        int textureID;				// The texture index for the brush

        public BSPBrush() {
            brushSide = loader.readInt();
            numOfBrushSides = loader.readInt();
            textureID = loader.readInt();
        }
    }

    // This stores the brush side data, which stores indices for the normal and texture ID
    public class BSPBrushSide {

        int plane;					// The plane index
        int textureID;				// The texture index

        public BSPBrushSide() {
            plane = loader.readInt();
            textureID = loader.readInt();
        }
    }

    ////////////////////////////FIND TEXTURE EXTENSION \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This attaches the image extension to the texture name, if found
    /////
    //////////////////////////// FIND TEXTURE EXTENSION \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private String findTextureExtension(String fileName) {

        File fileJpg = new File(fileName + ".jpg");
        File fileTga = new File(fileName + ".tga");
        // This function is a very basic way to find the extension
        // of the texture that is being passed in.  Quake saves it's
        // textures with just the name, and omits the extension.  I
        // still haven't figured out why they do this, but I imagine
        // it has to do with allowing you to change images to different 
        // image formats without having to use the level editor again.
        // What we do hear is just assume that it's either going to be
        // a jpeg or targa file.  I haven't seen any other type
        // be used.  If you just add on either one of those extensions
        // to the current name and see if a file with that name exits,
        // then it must be the texture extension.  If fopen() returns
        // a NULL, there is no file with that name.  Keep in mind that
        // most levels use the textures that come with Quake3.  That means
        // you won't be able to load them unless you try and read from their
        // pk3 files if the texture isn't found in the level's .pk3 file.
        // Also, I have found that some shader textures store the file name
        // in the shader.  So, don't be surprised if not all the textures are loaded.

        if (fileJpg.exists()) {
            return fileJpg.getAbsolutePath();
        } else if (fileTga.exists()) {
            return fileTga.getAbsolutePath();
        }

        // Otherwise, it must be a special texture or given in the shader file,
        // or possibly a base Quake texture used in the game.  There are some
        // special names like "textures\caulk" and such that mean special things.
        // They aren't actual textures.  This took me a lot of pulling hair to find this out.
        return null;
    }

    //////////////////////////// LOAD BSP \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This loads in all of the .bsp data for the level
    /////
    //////////////////////////// LOAD BSP \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public boolean loadBSP(String fileName, String factorGamma) throws IOException {
        File file = new File(fileName);

        if (file.exists()) {
            loader = new BinaryLoader(file);
        }

        // This is the bread and butter of our tutorial.  All the level
        // information is loaded from here.  The HTML file that comes with
        // this tutorial should explain about the .bsp file format, but let
        // me give a quick recap.
        //
        // The .bsp file format stores the data in lumps.  Look at the
        // tBSPLump enum to see all the lumps in their order.  A lump
        // is just like a chunk like in binary formats such as .3ds.
        // It stores an offset into the file for that lump, as well as
        // the lump's size in bytes.  We first want to read the BSP header,
        // then read in all the lump info.  We can then seek to the correct
        // offset for each lump and read in it's data.  To find out how many
        // structures instances the lump has, you just divide the lump's
        // length by the sizeof(<theStructure>).  Check out the HTML file for
        // more detail on this.  The only one that is handled a bit differently
        // is the visibility lump.  There is only one of these for the .bsp file.
        // Check if the .bsp file could be opened
        if (!file.exists()) {
            // Display an error message and quit if the file can't be found.
            LOGGER.info("Could not find BSP file!");
            return false;
        }

        // Initialize the header and lump structures
        BSPHeader header = null;
        BSPLump[] lumps = new BSPLump[Lumps.kMaxLumps.ordinal()];

        // Read in the header and lump data
        header = new BSPHeader();

        for (int j = 0; j < lumps.length; j++) {
            lumps[j] = new BSPLump();
        }

        // Now we know all the information about our file.  We can
        // then allocate the needed memory for our member variables.
        // Allocate the vertex memory
        numOfVerts = lumps[Lumps.kVertices.ordinal()].length / 44;
        verts = new BSPVertex[numOfVerts];

        // Allocate the face memory
        numOfFaces = lumps[Lumps.kFaces.ordinal()].length / 104;
        faces = new BSPFace[numOfFaces];

        // Allocate the index memory
        numOfIndices = lumps[Lumps.kIndices.ordinal()].length / 4;
        indices = new int[numOfIndices];

        // Allocate memory to read in the texture information.
        // We create a local pointer of BSPTextures because we don't need
        // that information once we create texture maps from it.
        numOfTextures = lumps[Lumps.kTextures.ordinal()].length / 72;
        textures = new BSPTexture[numOfTextures];

        // Allocate memory to read in the lightmap data.  Like the texture
        // data, we just need to create a local array to be destroyed real soon.
        numOfLightmaps = lumps[Lumps.kLightmaps.ordinal()].length / 49152;
        BSPLightMap[] lightmaps = new BSPLightMap[numOfLightmaps];

        // Seek to the position in the file that stores the vertex information
        loader.seekMarkOffset(lumps[Lumps.kVertices.ordinal()].offset);

        // Since Quake has the Z-axis pointing up, we want to convert the data so
        // that Y-axis is pointing up (like normal!) :)
        // Go through all of the vertices that need to be read
        for (int i = 0; i < numOfVerts; i++) {
            // Read in the current vertex
            verts[i] = new BSPVertex();
            // Swap the y and z values, and negate the new z so Y is up.
            float temp = verts[i].position.y;
            verts[i].position.y = verts[i].position.z;
            verts[i].position.z = -temp;
        }

        // Seek to the position in the file that stores the index information
        loader.seekMarkOffset(lumps[Lumps.kIndices.ordinal()].offset);

        // Read in all the index information
        for (int i = 0; i < indices.length; i++) {
            indices[i] = loader.readInt();
        }

        // Seek to the position in the file that stores the face information
        loader.seekMarkOffset(lumps[Lumps.kFaces.ordinal()].offset);

        // Read in all the face information
        for (int i = 0; i < numOfFaces; i++) {
            faces[i] = new BSPFace();
        }

        // Seek to the position in the file that stores the texture information
        loader.seekMarkOffset(lumps[Lumps.kTextures.ordinal()].offset);

        // Read in all the texture information
        for (int i = 0; i < numOfTextures; i++) {
            textures[i] = new BSPTexture();
        }

        // Now that we have the texture information, we need to load the
        // textures.  Since the texture names don't have an extension, we need
        // to find it first.
        // Go through all of the textures
        for (int i = 0; i < numOfTextures; i++) {
            // Find the extension if any and append it to the file name
            textures[i].textureName = findTextureExtension(textures[i].textureName);

            // Create a texture from the image
            // If there is a valid texture name passed in, we want to set the texture data
            if (textures[i].textureName != null) {
                texManager.getLoader().setPosition(i);
                texManager.getNormalImage(textures[i].textureName, false, false);
            } else {
                //int id = texManager.getTexture(i-1).getTexID();
                texManager.setTexture(new Texture(GL_TEXTURE_2D, 1));
            }
        }

        // Seek to the position in the file that stores the lightmap information
        //fseek(fp, lumps[kLightmaps].offset, SEEK_SET);
        loader.seekMarkOffset(lumps[Lumps.kLightmaps.ordinal()].offset);
        // Go through all of the lightmaps and read them in
        for (int i = 0; i < numOfLightmaps; i++) {
            // Read in the RGB data for each lightmap
            lightmaps[i] = new BSPLightMap();
            // Create a texture map for each lightmap that is read in.  The lightmaps
            // are always 128 by 128.
            createLightmapTexture(lightBuffer, i, lightmaps[i], 128, 128, factorGamma);
        }

        // In this function we read from a bunch of new lumps.  These include
        // the BSP nodes, the leafs, the leaf faces, BSP splitter planes and
        // visibility data (clusters).
        // Store the number of nodes and allocate the memory to hold them
        numOfNodes = lumps[Lumps.kNodes.ordinal()].length / 36;
        nodes = new BSPNode[numOfNodes];

        // Seek to the position in the file that hold the nodes and store them in m_pNodes
        loader.seekMarkOffset(lumps[Lumps.kNodes.ordinal()].offset);
        for (int i = 0; i < numOfNodes; i++) {
            nodes[i] = new BSPNode();
        }

        // Store the number of leafs and allocate the memory to hold them
        numOfLeafs = lumps[Lumps.kLeafs.ordinal()].length / 48;
        leafs = new BSPLeaf[numOfLeafs];

        // Seek to the position in the file that holds the leafs and store them in m_pLeafs
        loader.seekMarkOffset(lumps[Lumps.kLeafs.ordinal()].offset);
        // Now we need to go through and convert all the leaf bounding boxes
        // to the normal OpenGL Y up axis.
        for (int i = 0; i < numOfLeafs; i++) {
            leafs[i] = new BSPLeaf();

            int temp = leafs[i].min.y;
            leafs[i].min.y = leafs[i].min.z;
            leafs[i].min.z = -temp;

            // Swap the max y and z values, then negate the new Z
            temp = leafs[i].max.y;
            leafs[i].max.y = leafs[i].max.z;
            leafs[i].max.z = -temp;
        }

        // Store the number of leaf faces and allocate the memory for them
        numOfLeafFaces = lumps[Lumps.kLeafFaces.ordinal()].length / 4;
        leafFaces = new int[numOfLeafFaces];

        // Seek to the leaf faces lump, then read it's data
        //fseek(fp, lumps[kLeafFaces].offset, SEEK_SET);
        loader.seekMarkOffset(lumps[Lumps.kLeafFaces.ordinal()].offset);

        // Read in all the index information
        for (int i = 0; i < leafFaces.length; i++) {
            leafFaces[i] = loader.readInt();
        }

        // Store the number of planes, then allocate memory to hold them
        numOfPlanes = lumps[Lumps.kPlanes.ordinal()].length / 16;
        planes = new BSPPlane[numOfPlanes];

        // Seek to the planes lump in the file, then read them into m_pPlanes
        loader.seekMarkOffset(lumps[Lumps.kPlanes.ordinal()].offset);

        // Go through every plane and convert it's normal to the Y-axis being up
        for (int i = 0; i < numOfPlanes; i++) {
            planes[i] = new BSPPlane();

            float temp = planes[i].normal.y;
            planes[i].normal.y = planes[i].normal.z;
            planes[i].normal.z = -temp;
        }

        // Seek to the position in the file that holds the visibility lump
        loader.seekMarkOffset(lumps[Lumps.kVisData.ordinal()].offset);
        // Check if there is any visibility information first
        if (lumps[Lumps.kVisData.ordinal()].length > 0) {

            clusters = new BSPVisData();
        }

        // Like we do for other data, we read get the size of brushes and allocate memory
        numOfBrushes = lumps[Lumps.kBrushes.ordinal()].length / 4;
        brushes = new BSPBrush[numOfBrushes];

        // Here we read in the brush information from the BSP file
        //fseek(fp, lumps[kBrushes].offset, SEEK_SET);
        loader.seekMarkOffset(lumps[Lumps.kBrushes.ordinal()].offset);
        //fread(m_pBrushes, m_numOfBrushes, sizeof(tBSPBrush), fp);

        for (int i = 0; i < numOfBrushes; i++) {
            brushes[i] = new BSPBrush();
        }

        // Get the size of brush sides, then allocate memory for it
        numOfBrushSides = lumps[Lumps.kBrushSides.ordinal()].length / 4;
        brushSides = new BSPBrushSide[numOfBrushSides];

        // Read in the brush sides data
        //fseek(fp, lumps[kBrushSides].offset, SEEK_SET);
        loader.seekMarkOffset(lumps[Lumps.kBrushSides.ordinal()].offset);
        //fread(m_pBrushSides, m_numOfBrushSides, sizeof(tBSPBrushSide), fp);
        for (int i = 0; i < numOfBrushSides; i++) {
            brushSides[i] = new BSPBrushSide();
        }

        // Read in the number of leaf brushes and allocate memory for it
        numOfLeafBrushes = lumps[Lumps.kLeafBrushes.ordinal()].length / 4;
        leafBrushes = new int[numOfLeafBrushes];

        // Finally, read in the leaf brushes for traversing the bsp tree with brushes
        //fseek(fp, lumps[kLeafBrushes].offset, SEEK_SET);
        loader.seekMarkOffset(lumps[Lumps.kLeafBrushes.ordinal()].offset);
        //fread(m_pLeafBrushes, m_numOfLeafBrushes, sizeof(int), fp);
        for (int i = 0; i < numOfLeafBrushes; i++) {
            leafBrushes[i] = loader.readInt();
        }

        // I decided to put in a really big optimization for rendering.
        // I create a bitset that holds a bit slot for every face in the level.
        // Once the face is drawn, the slot saved for that face is set to 1.
        // If we try and draw it again, it checks first to see if it has already
        // been drawn.  We need to do this because many leafs stores faces that
        // are the same in other leafs.  If we don't check if it's already been drawn,
        // you can sometimes draw double the faces that you need to.  In this first
        // tutorial we draw every face once, so it doesn't matter, but when we get
        // into leafs we will need this.  I just choose to add it in the beginning
        // so I don't cram a ton of code down your throat when we get to the 
        // BSP nodes/leafs.
        // Here we allocate enough bits to store all the faces for our bitset
        facesDrawn = new BitSet();
        facesDrawn.resize(numOfFaces);

        // Return a success
        return true;
    }

    //////////////////////////////CREATE LIGHTMAP TEXTURE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This creates a texture map from the light map image bits
    /////
    ////////////////////////////// CREATE LIGHTMAP TEXTURE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void createLightmapTexture(IntBuffer texture, int position, BSPLightMap imageBits, int width, int height, String factorGamma) throws IOException {
        // This function takes in the lightmap image bits and creates a texture map
        // from them.  The width and height is usually 128x128 anyway....

        // Generate a texture with the associative texture ID stored in the array
        //IntBuffer temp = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        glGenTextures(lightBuffer);
        int textID = lightBuffer.get(position);
        //System.out.println(textID);
        texLight = new Texture(GL_TEXTURE_2D, textID);
        // This sets the alignment requirements for the start of each pixel row in memory.
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // Bind the texture to the texture arrays index and init the texture
        texLight.bind();
        texManagerLight.setTexture(texLight);

        // Change the lightmap gamma values by our desired gamma
        changeGamma(imageBits, imageBits.imageBits.length, factorGamma);

        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(imageBits.imageBits.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(imageBits.imageBits, 0, imageBits.imageBits.length);
        imageBuffer.flip();

        //Build Mipmaps (builds different versions of the picture for distances - looks better)
        gluBuild2DMipmaps(GL_TEXTURE_2D, 3, width, height, GL_RGB, GL_UNSIGNED_BYTE, imageBuffer);

        //Assign the mip map levels		
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    }

    ////////////////////////////CHANGE GAMMA \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This manually changes the gamma of an image
    /////
    //////////////////////////// CHANGE GAMMA \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void changeGamma(BSPLightMap light, int size, String factorGamma) {
        //  This function was taken from a couple engines that I saw,
        // which most likely originated from the Aftershock engine.
        // Kudos to them!  What it does is increase/decrease the intensity
        // of the lightmap so that it isn't so dark.  Quake uses hardware to
        // do this, but we will do it in code.

        float factor = Float.parseFloat(factorGamma);

        // Go through every pixel in the lightmap
        //for(int i = 0; i < size / 3; i++, pImage += 3) 
        for (int i = 0; i < size; i += 3) {
            float scale = 1.0f, temp = 0.0f;
            float r = 0, g = 0, b = 0;

            // extract the current RGB values
            r = (float) (light.imageBits[i] & 0xff);
            g = (float) (light.imageBits[i + 1] & 0xff);
            b = (float) (light.imageBits[i + 2] & 0xff);

            // Multiply the factor by the RGB values, while keeping it to a 255 ratio
            r = r * factor / 255.0f;
            g = g * factor / 255.0f;
            b = b * factor / 255.0f;

            // Check if the the values went past the highest value
            if (r > 1.0f && (temp = (1.0f / r)) < scale) {
                scale = temp;
            }
            if (g > 1.0f && (temp = (1.0f / g)) < scale) {
                scale = temp;
            }
            if (b > 1.0f && (temp = (1.0f / b)) < scale) {
                scale = temp;
            }

            // Get the scale for this pixel and multiply it by our pixel values
            scale *= 255.0f;
            r *= scale;
            g *= scale;
            b *= scale;

            // Assign the new gamma'nized RGB values to our image
            light.imageBits[i] = (byte) r;
            light.imageBits[i + 1] = (byte) g;
            light.imageBits[i + 2] = (byte) b;
        }
    }

    ////////////////////////////FIND LEAF \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This returns the leaf our camera is in
    /////
    //////////////////////////// FIND LEAF \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private int findLeaf(Vector position) {
        int i = 0;
        float distance = 0.0f;

        // This function takes in our camera position, then goes and walks
        // through the BSP nodes, starting at the root node, finding the leaf node
        // that our camera resides in.  This is done by checking to see if
        // the camera is in front or back of each node's splitting plane.
        // If the camera is in front of the camera, then we want to check
        // the node in front of the node just tested.  If the camera is behind
        // the current node, we check that nodes back node.  Eventually, this
        // will find where the camera is according to the BSP tree.  Once a
        // node index (i) is found to be a negative number, that tells us that
        // that index is a leaf node, not another BSP node.  We can either calculate
        // the leaf node index from -(i + 1) or ~1.  This is because the starting
        // leaf index is 0, and you can't have a negative 0.  It's important
        // for us to know which leaf our camera is in so that we know which cluster
        // we are in.  That way we can test if other clusters are seen from our cluster.
        // Continue looping until we find a negative index
        while (i >= 0) {
            // Get the current node, then find the slitter plane from that
            // node's plane index.  Notice that we use a constant reference
            // to store the plane and node so we get some optimization.
            BSPNode node = nodes[i];
            BSPPlane plane = planes[node.plane];

            // Use the Plane Equation (Ax + by + Cz + D = 0) to find if the
            // camera is in front of or behind the current splitter plane.
            distance = plane.normal.x * position.x
                    + plane.normal.y * position.y
                    + plane.normal.z * position.z - plane.distance;

            // If the camera is in front of the plane
            if (distance >= 0) {
                // Assign the current node to the node in front of itself
                i = node.front;
            } // Else if the camera is behind the plane
            else {
                // Assign the current node to the node behind itself
                i = node.back;
            }
        }

        // Return the leaf index (same thing as saying:  return -(i + 1)).
        return ~i;  // Binary operation
    }

    ////////////////////////////IS CLUSTER VISIBLE \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This tells us if the "current" cluster can see the "test" cluster
    /////
    //////////////////////////// IS CLUSTER VISIBLE \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private int isClusterVisible(int current, int test) {
        // This function is used to test the "current" cluster against
        // the "test" cluster.  If the "test" cluster is seen from the
        // "current" cluster, we can then draw it's associated faces, assuming
        // they aren't frustum culled of course.  Each cluster has their own
        // bitset containing a bit for each other cluster.  For instance, if there
        // is 10 clusters in the whole level (a tiny level), then each cluster
        // would have a bitset of 10 bits that store a 1 (visible) or a 0 (not visible) 
        // for each other cluster.  Bitsets are used because it's faster and saves
        // memory, instead of creating a huge array of booleans.  It seems that
        // people tend to call the bitsets "vectors", so keep that in mind too.

        // Make sure we have valid memory and that the current cluster is > 0.
        // If we don't have any memory or a negative cluster, return a visibility (1).
        if (clusters.bitSets == null || current < 0) {
            return 1;
        }

        // Use binary math to get the 8 bit visibility set for the current cluster
        byte visSet = clusters.bitSets[(current * clusters.bytesPerCluster) + (test / 8)];

        // Now that we have our vector (bitset), do some bit shifting to find if
        // the "test" cluster is visible from the "current" cluster, according to the bitset.
        int result = visSet & (1 << ((test) & 7));

        // Return the result ( either 1 (visible) or 0 (not visible) )
        return (result);
    }

    /////////////////////////////////// TRACE RAY \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This takes a start and end position (ray) to test against the BSP brushes
    /////
    /////////////////////////////////// TRACE RAY \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public Vector traceRay(Vector vStart, Vector vEnd) {
        // We don't use this function, but we set it up to allow us to just check a
        // ray with the BSP tree brushes.  We do so by setting the trace type to TYPE_RAY.
        traceType = TYPE_RAY;

        // Run the normal Trace() function with our start and end 
        // position and return a new position
        return trace(vStart, vEnd);
    }

    /////////////////////////////////// TRACE SPHERE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This tests a sphere around our movement vector against the BSP brushes for collision
    /////
    /////////////////////////////////// TRACE SPHERE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public Vector traceSphere(Vector vStart, Vector vEnd, float radius) {
        // In this tutorial we are doing sphere collision, so this is the function
        // that we will be doing to initiate our collision checks.

        // Here we initialize the type of trace (SPHERE) and initialize other data
        traceType = TYPE_SPHERE;
        m_bCollided = false;
        
        traceRadius = radius;

        // Get the new position that we will return to the camera or player
        Vector vNewPosition = trace(vStart, vEnd);
        
        // Return the new position to be changed for the camera or player
        return vNewPosition;
    }
    
    /////////////////////////////////// TRACE BOX \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
/////
/////	This takes a start and end position to test a AABB (box) against the BSP brushes
/////
/////////////////////////////////// TRACE BOX \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public Vector traceBox(Vector vStart, Vector vEnd, Vector vMin, Vector vMax) {
        traceType = TYPE_BOX;			// Set the trace type to a BOX
        traceMaxs = vMax;			// Set the max value of our AABB
        traceMins = vMin;			// Set the min value of our AABB
        m_bCollided = false;			// Reset the collised flag
        
        // Here is a little tricky piece of code that basically takes the largest values
        // of the min and max values and stores them in a vector called vExtents.  This means
        // that we are storing the largest size of our box along each x, y, z value.  We use this
        // as our offset (like with sphere collision) to determine if our box collides with
        // any brushes.  If you aren't familiar with these "Terse" operations, it means that
        // we check if (i.e): if(-m_vTraceMins.x > m_vTraceMaxs.x), then return -m_vTraceMins.x,
        // otherwise, take the positive of x: m_vTraceMins.x.  We do this for each x, y, z slot.
        // This is smaller code than doing a bunch of if statements... but yes, harder to read :)
        // Grab the extend of our box (the largest size for each x, y, z axis)
        extents = new Vector(-traceMins.x > traceMaxs.x ? -traceMins.x : traceMaxs.x,
                -traceMins.y > traceMaxs.y ? -traceMins.y : traceMaxs.y,
                -traceMins.z > traceMaxs.z ? -traceMins.z : traceMaxs.z);

        // Check if our movement m_bCollided with anything, then get back our new position
        Vector vNewPosition = trace(vStart, vEnd);

        // Return our new position
        return vNewPosition;
    }

    /////////////////////////////////// TRACE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This takes a start and end position (general) to test against the BSP brushes
    /////
    /////////////////////////////////// TRACE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public Vector trace(Vector vStart, Vector vEnd) {
        // Initially we set our trace ratio to 1.0f, which means that we don't have
        // a collision or intersection point, so we can move freely.
        traceRatio = 1.0f;

        // We start out with the first node (0), setting our start and end ratio to 0 and 1.
        // We will recursively go through all of the nodes to see which brushes we should check.
        checkNode(0, 0.0f, 1.0f, vStart, vEnd);

        // If the traceRatio is STILL 1.0f, then we never m_bCollided and just return our end position
        if (traceRatio == 1.0f) {
            return vEnd;
        } else // Else COLLISION!!!!
        {
            // If we get here then it's assumed that we m_bCollided and need to move the position
            // the correct distance from the starting position a position around the intersection
            // point.  This is done by the cool equation below (described in detail at top of page).

            // Set our new position to a position that is right up to the brush we m_bCollided with
            //Vector3f vNewPosition = vStart + ((vEnd - vStart) * traceRatio);
            Vector tempSubtract = vEnd.Subtract(vStart);

            Vector vNewPosition = tempSubtract.Multiply(traceRatio).Add(vStart);
            
            Vector vMove = vEnd.Subtract(vNewPosition);
            
            float distance = vMove.DotProduct(collisionNormal);
            
            Vector vEndPosition = vEnd.Subtract(collisionNormal.Multiply(distance));
            
            vNewPosition = trace(vNewPosition, vEndPosition);
            
            // Return the new position to be used by our camera (or player)
            return vNewPosition;
        }
    }

    /////////////////////////////////// CHECK NODE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This traverses the BSP to find the brushes closest to our position
    /////
    /////////////////////////////////// CHECK NODE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void checkNode(int nodeIndex, float startRatio, float endRatio, Vector vStart, Vector vEnd) {
        // Remember, the nodeIndices are stored as negative numbers when we get to a leaf, so we 
        // check if the current node is a leaf, which holds brushes.  If the nodeIndex is negative,
        // the next index is a leaf (note the: nodeIndex + 1)
        if (nodeIndex < 0) {
            // If this node in the BSP is a leaf, we need to negate and add 1 to offset
            // the real node index into the m_pLeafs[] array.  You could also do [~nodeIndex].
            BSPLeaf pLeaf = leafs[-(nodeIndex + 1)];

            // We have a leaf, so let's go through all of the brushes for that leaf
            for (int i = 0; i < pLeaf.numOfLeafBrushes; i++) {
                // Get the current brush that we going to check
                BSPBrush pBrush = brushes[leafBrushes[pLeaf.leafBrush + i]];

                // This is kind of an important line.  First, we check if there is actually
                // and brush sides (which store indices to the normal and plane data for the brush).
                // If not, then go to the next one.  Otherwise, we also check to see if the brush
                // is something that we want to collide with.  For instance, there are brushes for
                // water, lava, bushes, misc. sprites, etc...  We don't want to collide with water
                // and other things like bushes, so we check the texture type to see if it's a solid.
                // If the textureType can be binary-anded (&) and still be 1, then it's solid,
                // otherwise it's something that can be walked through.  That's how Quake chose to
                // do it.
                // Check if we have brush sides and the current brush is solid and collidable
                if ((pBrush.numOfBrushSides > 0) && (textures[pBrush.textureID].textureType & 1) == 1) {
                    // Now we delve into the dark depths of the real calculations for collision.
                    // We can now check the movement vector against our brush planes.
                    checkBrush(pBrush, vStart, vEnd);
                }
            }

            // Since we found the brushes, we can go back up and stop recursing at this level
            return;
        }

        // If we haven't found a leaf in the node, then we need to keep doing some dirty work
        // until we find the leafs which store the brush information for collision detection.
        // Grad the next node to work with and grab this node's plane data
        BSPNode pNode = nodes[nodeIndex];
        BSPPlane pPlane = planes[pNode.plane];

        // Now we do some quick tests to see which side we fall on of the node in the BSP
        // Here we use the plane equation to find out where our initial start position is
        // according the node that we are checking.  We then grab the same info for the end pos.
        float startDistance = vStart.DotProduct(pPlane.normal) - pPlane.distance;
        float endDistance = vEnd.DotProduct(pPlane.normal) - pPlane.distance;
        float offset = 0.0f;

        // If we are doing any type of collision detection besides a ray, we need to change
        // the offset for which we are testing collision against the brushes.  If we are testing
        // a sphere against the brushes, we need to add the sphere's offset when we do the plane
        // equation for testing our movement vector (start and end position).  * More Info * For
        // more info on sphere collision, check out our tutorials on this subject.
        // If we are doing sphere collision, include an offset for our collision tests below
        if (traceType == TYPE_SPHERE) {
            offset = traceRadius;
        } else if(traceType == TYPE_BOX) {
	           // This equation does a dot product to see how far our
            // AABB is away from the current plane we are checking.
            // Since this is a distance, we need to make it an absolute
            // value, which calls for the fabs() function (abs() for floats).

            // Get the distance our AABB is from the current splitter plane
            offset = (float) (Math.abs(extents.x * pPlane.normal.x)
                    + Math.abs(extents.y * pPlane.normal.y)
                    + Math.abs(extents.z * pPlane.normal.z));
        }
        // Below we just do a basic traversal down the BSP tree.  If the points are in
        // front of the current splitter plane, then only check the nodes in front of
        // that splitter plane.  Otherwise, if both are behind, check the nodes that are
        // behind the current splitter plane.  The next case is that the movement vector
        // is on both sides of the splitter plane, which makes it a bit more tricky because we now
        // need to check both the front and the back and split up the movement vector for both sides.

        // Here we check to see if the start and end point are both in front of the current node.
        // If so, we want to check all of the nodes in front of this current splitter plane.
        if (startDistance >= offset && endDistance >= offset) {
            // Traverse the BSP tree on all the nodes in front of this current splitter plane
            checkNode(pNode.front, startDistance, endDistance, vStart, vEnd);
        } // If both points are behind the current splitter plane, traverse down the back nodes
        else if (startDistance < -offset && endDistance < -offset) {
            // Traverse the BSP tree on all the nodes in back of this current splitter plane
            checkNode(pNode.back, startDistance, endDistance, vStart, vEnd);
        } else {
            // If we get here, then our ray needs to be split in half to check the nodes
            // on both sides of the current splitter plane.  Thus we create 2 ratios.
            float Ratio1 = 1.0f, Ratio2 = 0.0f, middleRatio = 0.0f;
            Vector vMiddle = new Vector();	// This stores the middle point for our split ray

            // Start of the side as the front side to check
            int side = pNode.front;

            // Here we check to see if the start point is in back of the plane (negative)
            if (startDistance < endDistance) {
                // Since the start position is in back, let's check the back nodes
                side = pNode.back;

                // Here we create 2 ratios that hold a distance from the start to the
                // extent closest to the start (take into account a sphere and epsilon).
                // We use epsilon like Quake does to compensate for float errors.  The second
                // ratio holds a distance from the other size of the extents on the other side
                // of the plane.  This essential splits the ray for both sides of the splitter plane.
                float inverseDistance = 1.0f / (startDistance - endDistance);
                Ratio1 = (startDistance - offset - EPSILON) * inverseDistance;
                Ratio2 = (startDistance + offset + EPSILON) * inverseDistance;
            } // Check if the starting point is greater than the end point (positive)
            else if (startDistance > endDistance) {
                // This means that we are going to recurse down the front nodes first.
                // We do the same thing as above and get 2 ratios for split ray.
                // Ratio 1 and 2 are switched in contrast to the last if statement.
                // This is because the start is starting in the front of the splitter plane.
                float inverseDistance = 1.0f / (startDistance - endDistance);
                Ratio1 = (startDistance + offset + EPSILON) * inverseDistance;
                Ratio2 = (startDistance - offset - EPSILON) * inverseDistance;
            }

            // Make sure that we have valid numbers and not some weird float problems.
            // This ensures that we have a value from 0 to 1 as a good ratio should be :)
            if (Ratio1 < 0.0f) {
                Ratio1 = 0.0f;
            } else if (Ratio1 > 1.0f) {
                Ratio1 = 1.0f;
            }

            if (Ratio2 < 0.0f) {
                Ratio2 = 0.0f;
            } else if (Ratio2 > 1.0f) {
                Ratio2 = 1.0f;
            }

            // Just like we do in the Trace() function, we find the desired middle
            // point on the ray, but instead of a point we get a middleRatio percentage.
            // This isn't the true middle point since we are using offset's and the epsilon value.
            // We also grab the middle point to go with the ratio.
            middleRatio = startRatio + ((endRatio - startRatio) * Ratio1);
            //vMiddle = vStart + ((vEnd - vStart) * Ratio1);
            vMiddle = vEnd.Subtract(vStart).Multiply(Ratio1).Add(vStart);

            // Now we recurse on the current side with only the first half of the ray
            checkNode(side, startRatio, middleRatio, vStart, vMiddle);

            // Now we need to make a middle point and ratio for the other side of the node
            middleRatio = startRatio + ((endRatio - startRatio) * Ratio2);
            //vMiddle = vStart + ((vEnd - vStart) * Ratio2);
            vMiddle = vEnd.Subtract(vStart).Multiply(Ratio2).Add(vStart);

            // Depending on which side should go last, traverse the bsp with the
            // other side of the split ray (movement vector).
            if (side == pNode.back) {
                checkNode(pNode.front, middleRatio, endRatio, vMiddle, vEnd);
            } else {
                checkNode(pNode.back, middleRatio, endRatio, vMiddle, vEnd);
            }
        }
    }

    /////////////////////////////////// CHECK BRUSH \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This checks our movement vector against all the planes of the brush
    /////
    /////////////////////////////////// CHECK BRUSH \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void checkBrush(BSPBrush pBrush, Vector vStart, Vector vEnd) {
        float startRatio = -1.0f;		// Like in BrushCollision.htm, start a ratio at -1
        float endRatio = 1.0f;			// Set the end ratio to 1
        boolean startsOut = false;			// This tells us if we starting outside the brush

        // This function actually does the collision detection between our movement
        // vector and the brushes in the world data.  We will go through all of the
        // brush sides and check our start and end ratio against the planes to see if
        // they pass each other.  We start the startRatio at -1 and the endRatio at
        // 1, but as we set the ratios to their intersection points (ratios), then
        // they slowly move toward each other.  If they pass each other, then there
        // is definitely not a collision.
        // Go through all of the brush sides and check collision against each plane
        for (int i = 0; i < pBrush.numOfBrushSides; i++) {
            // Here we grab the current brush side and plane in this brush
            BSPBrushSide pBrushSide = brushSides[pBrush.brushSide + i];
            BSPPlane pPlane = planes[pBrushSide.plane];

            // Let's store a variable for the offset (like for sphere collision)
            float offset = 0.0f;

            // If we are testing sphere collision we need to add the sphere radius
            if (traceType == TYPE_SPHERE) {
                offset = traceRadius;
            }
            // Test the start and end points against the current plane of the brush side.
            // Notice that we add an offset to the distance from the origin, which makes
            // our sphere collision work.

            float startDistance = vStart.DotProduct(pPlane.normal) - (pPlane.distance + offset);
            float endDistance = vEnd.DotProduct(pPlane.normal) - (pPlane.distance + offset);

            // Store the offset that we will check against the plane
            Vector vOffset = new Vector(0, 0, 0);

            // If we are using AABB collision
            if (traceType == TYPE_BOX) {
                // Grab the closest corner (x, y, or z value) that is closest to the plane
                vOffset.x = (pPlane.normal.x < 0) ? traceMaxs.x : traceMins.x;
                vOffset.y = (pPlane.normal.y < 0) ? traceMaxs.y : traceMins.y;
                vOffset.z = (pPlane.normal.z < 0) ? traceMaxs.z : traceMins.z;

                // Use the plane equation to grab the distance our start position is from the plane.
                // We need to add the offset to this to see if the box collides with the plane,
                // even if the position doesn't.
                startDistance = vStart.Add(vOffset).DotProduct(pPlane.normal) - pPlane.distance;

                // Get the distance our end position is from this current brush plane
                endDistance = vEnd.Add(vOffset).DotProduct(pPlane.normal) - pPlane.distance;
            }

            
            // Make sure we start outside of the brush's volume
            if (startDistance > 0) {
                startsOut = true;
            }

            // Stop checking since both the start and end position are in front of the plane
            if (startDistance > 0 && endDistance > 0) {
                return;
            }

            // Continue on to the next brush side if both points are behind or on the plane
            if (startDistance <= 0 && endDistance <= 0) {
                continue;
            }

            // If the distance of the start point is greater than the end point, we have a collision!
            if (startDistance > endDistance) {
                // This gets a ratio from our starting point to the approximate collision spot
                float Ratio1 = (startDistance - EPSILON) / (startDistance - endDistance);

                // If this is the first time coming here, then this will always be true,
                // since startRatio starts at -1.0f.  We want to find the closest collision,
                // so we still continue to check all of the brushes before quitting.
                if (Ratio1 > startRatio) {
                    // Set the startRatio (currently the closest collision distance from start)
                    startRatio = Ratio1;
                    m_bCollided = true;		// Let us know we m_bCollided!
                    collisionNormal = pPlane.normal;
                }
            } else {
                // Get the ratio of the current brush side for the endRatio
                float Ratio = (startDistance + EPSILON) / (startDistance - endDistance);

                // If the ratio is less than the current endRatio, assign a new endRatio.
                // This will usually always be true when starting out.
                if (Ratio < endRatio) {
                    endRatio = Ratio;
                }
            }
        }

        // If we didn't start outside of the brush we don't want to count this collision - return;
        if (startsOut == false) {
            return;
        }

        // If our startRatio is less than the endRatio there was a collision!!!
        if (startRatio < endRatio) {
            // Make sure the startRatio moved from the start and check if the collision
            // ratio we just got is less than the current ratio stored in traceRatio.
            // We want the closest collision to our original starting position.
            if (startRatio > -1 && startRatio < traceRatio) {
                // If the startRatio is less than 0, just set it to 0
                if (startRatio < 0) {
                    startRatio = 0;
                }

                // Store the new ratio in our member variable for later
                traceRatio = startRatio;
            }
        }
    }

    ////////////////////////////RENDER FACE \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This renders a face, determined by the passed in index
    /////
    //////////////////////////// RENDER FACE \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void renderFace(int faceIndex) {
        // Here we grab the face from the index passed in
        BSPFace face = faces[faceIndex];

        // Now, in this function you don't might get all messed up and confused with
        // what function is for vertex arrays and which function is for multi-texturing.
        // The gl*Pointer() and glEnableClientState() functions are for vertex arrays.
        // The glActiveTextureARG() and glClientActiveTextureARB() stuff is for multi-texturing.  
        // Since we allow the user to right or left click the mouse, turning on and off the 
        // light maps and textures, we need to make those checks in this function to know 
        // what we should render.
        glVertexPointer(3, 0, face.vertFloatBuffer);
        //glVertexPointer(3, GL_FLOAT, sizeof(tBSPVertex), &(m_pVerts[pFace->startVertIndex].vPosition));
        glEnableClientState(GL_VERTEX_ARRAY);
        // Next, we pass in the address of the first texture coordinate.  We also tell 
        // OpenGL that there are 2 UV coordinates that are floats, and the offset between 
        // each texture coordinate is the size of tBSPVertex in bytes.  
        // We need to them give an address to the start of this face's indices, startVertIndex.

        // If we want to render the textures
        if (isTextures()) {
            // Set the current pass as the first texture (For multi-texturing)
            glActiveTextureARB(GL_TEXTURE0_ARB);

            // Since we are using vertex arrays, we need to tell OpenGL which texture
            // coordinates to use for each texture pass.  We switch our current texture
            // to the first one, then set our texture coordinates.
            glClientActiveTextureARB(GL_TEXTURE0_ARB);

            glTexCoordPointer(2, 0, face.texFloatBuffer);

            // Set our vertex array client states for allowing texture coordinates
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            // Turn on texture arrays for the first pass
            glClientActiveTextureARB(GL_TEXTURE0_ARB);

            // To enable each texture pass, we want to turn on the texture coord array
            // state for each pass.  This needs to be done since we are using vertex arrays.
            glEnable(GL_TEXTURE_2D);
            //System.out.println(face.textureID);
            //glBindTexture(GL_TEXTURE_2D, texManager.getLoader().getTexID(face.textureID));
            //System.out.println("Size: "+texManager.getSize());
            texManager.getTexture(face.textureID).bind();
        }

        // If we want to render the textures
        if (isHasLightmaps() && face.lightmapID >= 0) {
            // Set the current pass as the second lightmap texture_
            glActiveTextureARB(GL_TEXTURE1_ARB);

            // Turn on texture arrays for the second lightmap pass
            glClientActiveTextureARB(GL_TEXTURE1_ARB);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            // Next, we need to specify the UV coordinates for our lightmaps.  This is done
            // by switching to the second texture and giving OpenGL our lightmap array.
            glClientActiveTextureARB(GL_TEXTURE1_ARB);

            glTexCoordPointer(2, 0, face.lightFloatBuffer);

            // Turn on texture mapping and bind the face's texture map
            glEnable(GL_TEXTURE_2D);
            //texLight.bind();
            texManagerLight.getTexture(face.lightmapID).bind();

        }

        // Now, to draw the face with vertex arrays we just need to tell OpenGL
        // which indices we want to draw and what primitive the format is in.
        // The faces are stored in triangles.  We give glDrawElements() a pointer
        // to our indices, but it's not a normal indice array.  The indices are stored
        // according to the pFace->startVertIndex into the vertices array.  If you were
        // to print all of our indices out, they wouldn't go above the number 5.  If there
        // is over 70 vertices though, how is that possible for the indices to work?  Well,
        // that is why we give our vertex array functions above a pointer to the startVertIndex,
        // then the indice array acts according to that.  This is very important to do it this
        // way, otherwise we will not get more than 5 vertices display, and for all our faces.
        // We are going to draw triangles, pass in the number of indices for this face, then
        // say the indices are stored as ints, then pass in the starting address in our indice
        // array for this face by indexing it by the startIndex variable of our current face.
        glDrawElements(GL_TRIANGLES, face.indiceIntBuffer);
        //glDrawElements(GL_TRIANGLES, pFace->numOfIndices, GL_UNSIGNED_INT, &(m_pIndices[pFace->startIndex]) );

    }

    //////////////////////////// RENDER LEVEL \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	Goes through all of the faces and draws them if the type is FACE_POLYGON
    /////
    //////////////////////////// RENDER LEVEL \\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void renderLevel(Vector position) {

        // Reset our bitset so all the slots are zero.
        facesDrawn.clearAll();

        /////// * /////////// * /////////// * NEW * /////// * /////////// * /////////// *
        // In this new revision of RenderLevel(), we do things a bit differently.
        // Instead of looping through all the faces, we now want to loop through
        // all of the leafs.  Each leaf stores a list of faces assign to it.
        // We call FindLeaf() to find the current leaf index that our camera is
        // in.  This leaf will then give us the cluster that the camera is in.  The
        // cluster is then used to test visibility between our current cluster
        // and other leaf clusters.  If another leaf's cluster is visible from our 
        // current cluster, the leaf's bounding box is checked against our frustum.  
        // Assuming the bounding box is inside of our frustum, we draw all the faces
        // stored in that leaf.  
        // Grab the leaf index that our camera is in
        int leafIndex = findLeaf(position);

        // Grab the cluster that is assigned to the leaf
        int cluster = leafs[leafIndex].cluster;

        // Initialize our counter variables (start at the last leaf and work down)
        int i = numOfLeafs;
        visibleFaces = 0;

        // Go through all the leafs and check their visibility
        while (i-- > 0) {
            // Get the current leaf that is to be tested for visibility from our camera's leaf
            BSPLeaf leaf = leafs[i];

            // If the current leaf can't be seen from our cluster, go to the next leaf
            if (isClusterVisible(cluster, leaf.cluster) == 0) {
                continue;
            }

            // If the current leaf is not in the camera's frustum, go to the next leaf
            if (!GameCore.gFrustum.boxInFrustum((float) leaf.min.x, (float) leaf.min.y, (float) leaf.min.z,
                    (float) leaf.max.x, (float) leaf.max.y, (float) leaf.max.z)) {
                continue;
            }

            // If we get here, the leaf we are testing must be visible in our camera's view.
            // Get the number of faces that this leaf is in charge of.
            int faceCount = leaf.numOfLeafFaces;

            // Loop through and render all of the faces in this leaf
            while (faceCount-- > 0) {
                // Grab the current face index from our leaf faces array
                int faceIndex = leafFaces[leaf.leafFace + faceCount];

                // Before drawing this face, make sure it's a normal polygon
                if (faces[faceIndex].type != FACE_POLYGON) {
                    continue;
                }

                // Since many faces are duplicated in other leafs, we need to
                // make sure this face already hasn't been drawn.
                if (facesDrawn.on(faceIndex)) {
                    // Increase the rendered face count to display for fun
                    visibleFaces++;
                    //System.out.println(visibleFaces);
                    // Set this face as drawn and render it
                    facesDrawn.set(faceIndex);
                    renderFace(faceIndex);
                }
            }
        }
    }

    public void setNumOfVerts(int numOfVerts) {
        this.numOfVerts = numOfVerts;
    }

    public int getNumOfVerts() {
        return numOfVerts;
    }

    public void setNumOfFaces(int numOfFaces) {
        this.numOfFaces = numOfFaces;
    }

    public int getNumOfFaces() {
        return numOfFaces;
    }

    public void setNumOfIndices(int numOfIndices) {
        this.numOfIndices = numOfIndices;
    }

    public int getNumOfIndices() {
        return numOfIndices;
    }

    public void setNumOfTextures(int numOfTextures) {
        this.numOfTextures = numOfTextures;
    }

    public int getNumOfTextures() {
        return numOfTextures;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setVerts(BSPVertex[] verts) {
        this.verts = verts;
    }

    public BSPVertex[] getVerts() {
        return verts;
    }

    public void setFaces(BSPFace[] faces) {
        this.faces = faces;
    }

    public BSPFace[] getFaces() {
        return faces;
    }

    public void setFacesDrawn(BitSet facesDrawn) {
        this.facesDrawn = facesDrawn;
    }

    public BitSet getFacesDrawn() {
        return facesDrawn;
    }

    public void setHasTextures(boolean renderFill) {
        this.hasTextures = renderFill;
    }

    public boolean isTextures() {
        return hasTextures;
    }

    public void setNumOfLightmaps(int numOfLightmaps) {
        this.numOfLightmaps = numOfLightmaps;
    }

    public int getNumOfLightmaps() {
        return numOfLightmaps;
    }

    public void setHasLightmaps(boolean hasLightmaps) {
        this.hasLightmaps = hasLightmaps;
    }

    public boolean isHasLightmaps() {
        return hasLightmaps;
    }

    public void setNumOfBrushes(int numOfBrushes) {
        this.numOfBrushes = numOfBrushes;
    }

    public int getNumOfBrushes() {
        return numOfBrushes;
    }

    public void setNumOfBrushSides(int numOfBrushSides) {
        this.numOfBrushSides = numOfBrushSides;
    }

    public int getNumOfBrushSides() {
        return numOfBrushSides;
    }

    public void setNumOfLeafBrushes(int numOfLeafBrushes) {
        this.numOfLeafBrushes = numOfLeafBrushes;
    }

    public int getNumOfLeafBrushes() {
        return numOfLeafBrushes;
    }

    public void setTraceType(int traceType) {
        this.traceType = traceType;
    }

    public int getTraceType() {
        return traceType;
    }

    public void setTraceRatio(float traceRatio) {
        this.traceRatio = traceRatio;
    }

    public float getTraceRatio() {
        return traceRatio;
    }

    public void setTraceRadius(float traceRadius) {
        this.traceRadius = traceRadius;
    }

    public float getTraceRadius() {
        return traceRadius;
    }

    public void setM_bCollided(boolean m_bCollided) {
        this.m_bCollided = m_bCollided;
    }

    public boolean isM_bCollided() {
        return m_bCollided;
    }
}
