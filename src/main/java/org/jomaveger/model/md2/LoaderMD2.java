package org.jomaveger.model.md2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.jomaveger.graphics.AnimationInfo;
import org.jomaveger.graphics.MaterialInfo;
import org.jomaveger.graphics.Object3D;
import org.jomaveger.math.Vector;
import org.jomaveger.texture.Texture;
import org.jomaveger.texture.TextureManager;
import org.jomaveger.util.LEDataInputStream;

/**
 * @author jmvegas.gertrudix
 */
public class LoaderMD2 {
    
    private final static Logger LOGGER = Logger.getLogger(LoaderMD2.class);

    // These are the needed defines for the max values when loading .MD2 files
    public static final int MD2_MAX_TRIANGLES = 4096;
    public static final int MD2_MAX_VERTICES = 2048;
    public static final int MD2_MAX_TEXCOORDS = 2048;
    public static final int MD2_MAX_FRAMES = 512;
    public static final int MD2_MAX_SKINS = 32;
    public static final int MD2_MAX_FRAMESIZE = (MD2_MAX_VERTICES * 4 + 128);

    // Member variables		
    private HeaderMD2 header;			// The header data
    private String[] skins;			// The skin data
    private TexCoordMD2[] texCoords;		// The texture coordinates
    private FaceMD2[] triangles;		// Face index information
    private FrameMD2[] frames;			// The frames of animation (vertices)
    private final TextureManager texManager = new TextureManager();
    private LEDataInputStream inputStream;
    private int offsetBytes = 0;

    // This holds the header information that is read in at the beginning of the file
    private class HeaderMD2 {

        int magic;					// This is used to identify the file
        int version;					// The version number of the file (Must be 8)
        int skinWidth;				// The skin width in pixels
        int skinHeight;				// The skin height in pixels
        int frameSize;				// The size in bytes the frames are
        int numSkins;				// The number of skins associated with the model
        int numVertices;				// The number of vertices (constant for each frame)
        int numTexCoords;			// The number of texture coordinates
        int numTriangles;			// The number of faces (polygons)
        int numGlCommands;			// The number of gl commands
        int numFrames;				// The number of animation frames
        int offsetSkins;				// The offset in the file for the skin data
        int offsetTexCoords;			// The offset in the file for the texture data
        int offsetTriangles;			// The offset in the file for the face data
        int offsetFrames;			// The offset in the file for the frames data
        int offsetGlCommands;		// The offset in the file for the gl commands data
        int offsetEnd;				// The end of the file offset

        HeaderMD2() throws IOException {

            magic = inputStream.readLEInt();					// This is used to identify the file
            version = inputStream.readLEInt();				// The version number of the file (Must be 8)
            skinWidth = inputStream.readLEInt();			// The skin width in pixels
            skinHeight = inputStream.readLEInt();				// The skin height in pixels
            frameSize = inputStream.readLEInt();				// The size in bytes the frames are
            numSkins = inputStream.readLEInt();			// The number of skins associated with the model
            numVertices = inputStream.readLEInt();				// The number of vertices (constant for each frame)
            numTexCoords = inputStream.readLEInt();			// The number of texture coordinates
            numTriangles = inputStream.readLEInt();		// The number of faces (polygons)
            numGlCommands = inputStream.readLEInt();		// The number of gl commands
            numFrames = inputStream.readLEInt();				// The number of animation frames
            offsetSkins = inputStream.readLEInt();			// The offset in the file for the skin data
            offsetTexCoords = inputStream.readLEInt();		// The offset in the file for the texture data
            offsetTriangles = inputStream.readLEInt();		// The offset in the file for the face data
            offsetFrames = inputStream.readLEInt();			// The offset in the file for the frames data
            offsetGlCommands = inputStream.readLEInt();		// The offset in the file for the gl commands data
            offsetEnd = inputStream.readLEInt();

            offsetBytes += 68;
        }
    };

    // This stores the normals and vertices for the frames
    private class TriangleMD2 {
        Vector vertex = new Vector();
        Vector normal = new Vector();
    };

    // This stores the indices into the vertex and texture coordinate arrays
    private class FaceMD2 {
        int[] vertexIndices = new int[3];
        int[] textureIndices = new int[3];

        protected FaceMD2() throws IOException {
            vertexIndices = new int[]{inputStream.readLEShort(), inputStream.readLEShort(),
                inputStream.readLEShort()};
            textureIndices = new int[]{inputStream.readLEShort(), inputStream.readLEShort(),
                inputStream.readLEShort()};

            offsetBytes += 12;
        }
    };

    // This stores UV coordinates
    private class TexCoordMD2 {

        float u, v;

        protected TexCoordMD2() throws IOException {
            u = (float) inputStream.readLEShort();
            v = (float) inputStream.readLEShort();

            offsetBytes += 4;

        }
    };

    // This stores the animation scale, translation and name information for a frame, plus verts
    private class AliasFrameMD2 {
        private Vector scale = new Vector();
        private Vector translate = new Vector();
        private String name;

        AliasFrameMD2() throws IOException {

            scale.x = inputStream.readLEFloat();
            scale.y = inputStream.readLEFloat();
            scale.z = inputStream.readLEFloat();

            translate.x = inputStream.readLEFloat();
            translate.y = inputStream.readLEFloat();
            translate.z = inputStream.readLEFloat();

            offsetBytes += 24;

            name = inputStream.readString();
            offsetBytes += 16;
            inputStream.skipBytes(16 - name.length() - 1);
        }
    };

    // This stores the frames vertices after they have been transformed
    private class FrameMD2 {
        String strName;
        TriangleMD2[] pVertices;

        protected FrameMD2() {
        }
    };

    ///////////////////////////////// IMPORT MD2 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This is called by the client to open the .Md2 file, read it, then clean up
    /////
    ///////////////////////////////// IMPORT MD2 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public boolean importMD2(ModelMD2 model, String fileName, String textureName) {

        try {

            // Open the MD2 file in binary
            File file = new File(fileName);

            FileInputStream inStream = new FileInputStream(file);

            inputStream = new LEDataInputStream(inStream);

            // Read the header data and store it in our m_Header member variable
            header = new HeaderMD2();

            // Make sure the ID == IDP3 and the version is this crazy number '15' or else it's a bad egg
            if (header.version != 8) {
                LOGGER.info("[Error]: file " + fileName + " version is not valid.");
                throw new RuntimeException("[Error]: file " + fileName + " version is not valid.");
            }

            // Now that we made sure the header had correct data, we want to read in the
            // rest of the data.  Once the data is read in, we need to convert it to our structures.
            // Since we are only reading in the first frame of animation, there will only
            // be ONE object in our structure, held within our variable.
            readMD2Data();
            
            // Here we pass in our model structure to it can store the read Quake data
            // in our own model and object structure data
            convertDataStructures(model);
            
            // If there is a valid texture name passed in, we want to set the texture data
            if (textureName != null) {
                // Create a local material info structure
                MaterialInfo texture = new MaterialInfo();

                Texture tex = texManager.getFlippedImage(textureName, true, false);

                texture.setTex(tex);
                
                // Copy the name of the file into our texture file name variable
                texture.setTexFile(tex.getName());

                // Since there is only one texture for a .Md2 file, the ID is always 0
                texture.setTexureId(tex.getTexID());

                // The tile or scale for the UV's is 1 to 1 (but Quake saves off a 0-256 ratio)
                texture.setUTile(1);
                texture.setVTile(1);

                // Add the local material info to our model's material list
                model.addMaterials(texture);
                model.getObject(model.getMaterials().size() - 1).setMaterialID(tex.getTexID());

            }

        } catch (IOException | RuntimeException e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);            
        }

        // Return a success
        return true;
    }

    ///////////////////////////////// READ MD2 DATA \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This function reads in all of the model's data, except the animation frames
    /////
    ///////////////////////////////// READ MD2 DATA \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void readMD2Data() throws IOException {

        try {
            // Here we allocate all of our memory from the header's information
            skins = new String[header.numSkins];
            texCoords = new TexCoordMD2[header.numTexCoords];
            triangles = new FaceMD2[header.numTriangles];
            frames = new FrameMD2[header.numFrames];

            //start with skins. Move the file pointer to the correct position.
            //Read in each skin for this model
            for (int j = 0; j < header.numSkins; j++) {
                //skins[j] = loader.readString(64);
                skins[j] = inputStream.readString();
                offsetBytes += 64;
                inputStream.skipBytes(64 - skins[j].length() - 1);
            }

            // Now read in texture coordinates.
            inputStream.skipBytes(header.offsetTexCoords - offsetBytes);
            for (int j = 0; j < header.numTexCoords; j++) {
                texCoords[j] = new TexCoordMD2();
            }

            // read the vertex data.
            inputStream.skipBytes(header.offsetTriangles - offsetBytes);
            for (int j = 0; j < header.numTriangles; j++) {
                triangles[j] = new FaceMD2();
            }

            inputStream.skipBytes(header.offsetFrames - offsetBytes);

            // Each keyframe has the same type of data, so read each
            // keyframe one at a time.
            for (int i = 0; i < header.numFrames; i++) {
                AliasFrameMD2 frame = new AliasFrameMD2();
                frames[i] = new FrameMD2();

                frames[i].pVertices = new TriangleMD2[header.numVertices];
                Vector[] aliasVertices = new Vector[header.numVertices];
                int[] aliasLightNormals = new int[header.numVertices];

                // Read in the first frame of animation
                for (int j = 0; j < header.numVertices; j++) {
                    aliasVertices[j] = new Vector(inputStream.readByte() & 0xFF, inputStream.readByte() & 0xFF, inputStream.readByte() & 0xFF);
                    aliasLightNormals[j] = inputStream.readByte() & 0xFF;
                    offsetBytes += 4;

                }

                // Copy the name of the animation to our frames array
                frames[i].strName = frame.name;
                TriangleMD2[] verices = frames[i].pVertices;

                for (int j = 0; j < header.numVertices; j++) {
                    verices[j] = new TriangleMD2();
                    verices[j].vertex.x = aliasVertices[j].x * frame.scale.x
                            + frame.translate.x;
                    verices[j].vertex.z = -1
                            * (aliasVertices[j].y * frame.scale.y + frame.translate.y);
                    verices[j].vertex.y = aliasVertices[j].z * frame.scale.z
                            + frame.translate.z;
                }
            }

        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }

    ///////////////////////////////// PARSE ANIMATIONS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This function fills in the animation list for each animation by name and frame
    /////
    ///////////////////////////////// PARSE ANIMATIONS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void parseAnimations(ModelMD2 model) {
        AnimationInfo animation = new AnimationInfo();
        String lastName = "";

        // This function felt like a hack when I wrote it.  You aren't really given
        // any good information about the animations, other than the fact that each
        // key frame has a name assigned to it with a frame number for that animation.
        // For instance, the first animation is the "stand" animation.  The first frame
        // would have the name of: "stand01" or perhaps "stand1".  The 40th frame is
        // usually the last frame for the standing animation, so it would look like:
        // "stand40".  After this frame, the next animation is the running animation.
        // The next frame is labeled something like "run01".  You now know that the
        // standing animation is from frame 1 to frame 40 of the total frames.  The
        // start of the run animation is 41.  We will know how long the run animation
        // goes when we run into another animation name besides "run..".  That is how
        // I went about finding out the animation information.  I just grab each frame
        // name and check if it's the same animation name as the last name we found.
        // If it is, I just ignore it and continue to the next frame.  Once I find that
        // it's not, I then have the last frame saved off from the index 'i', and then
        // I create a new animation to add to the list, then start from the beginning.
        // It wasn't until later that I found on www.planetquake.com that there is a
        // standard frame count for each animation and they are all the same.  I figure
        // this way makes it modular so you don't have to stick to those standards anyway.
        // Go through all of the frames of animation and parse each animation
        for (int i = 0; i < model.getNumOfObjects(); i++) {
            // Assign the name of this frame of animation to a string object
            String name = frames[i].strName;
            char[] temp = name.toCharArray();
            int frameNum = 0;

            // Go through and extract the frame numbers and erase them from the name
            for (int j = 0; j < temp.length; j++) {

                // If the current index is a number and it's one of the last 2 characters of the name
                if (Character.isDigit(temp[j]) && j >= temp.length - 2) {
                    
                    frameNum = Integer.parseInt(name.substring(j, temp.length));

                    name = name.substring(0, j);
                    break;
                }
            }

            // Check if this animation name is not the same as the last frame,
            // or if we are on the last frame of animation for this model
            if (!name.equalsIgnoreCase(lastName) || i == model.getObject().size() - 1) {
                // If this animation frame is NOT the first frame
                if (!lastName.equals("")) {
                    // Copy the last animation name into our new animation's name
                    animation.setAnimName(lastName);

                    // Set the last frame of this animation to i
                    animation.setEndFrame(i);

                    // Add the animation to our list and reset the animation object for next time
                    model.addAnimations(animation);
                    animation = new AnimationInfo();
                }

                // Set the starting frame number to the current frame number we just found,
                // minus 1 (since 0 is the first frame) and add 'i'.
                animation.setStartFrame(frameNum - 1 + i);
            }

            // Store the current animation name in the strLastName string to check it latter
            lastName = name;
        }
    }

    ///////////////////////////////// CONVERT DATA STRUCTURES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This function converts the .md2 structures to our own model and object structures
    /////
    ///////////////////////////////// CONVERT DATA STRUCTURES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void convertDataStructures(ModelMD2 model) {
        int j = 0;

        // Assign the number of objects, which is 1 since we only want 1 frame
        // of animation.  In the next tutorial each object will be a key frame
        // to interpolate between.
        model.setNumOfObjects(header.numFrames);

        //Create our animation list and store it in our model
        parseAnimations(model);

        for (int i = 0; i < model.getNumOfObjects(); i++) {
            // Create a local object to store the first frame of animation's data
            Object3D currentFrame = new Object3D();

            // Assign the vertex, texture coord and face count to our new structure
            currentFrame.setNumVertices(header.numVertices);
            currentFrame.setNumVert(header.numVertices);

            // Go through all of the vertices and assign them over to our structure
            for (j = 0; j < currentFrame.getNumVertices(); j++) {
                Vector temp = new Vector(frames[i].pVertices[j].vertex.x, frames[i].pVertices[j].vertex.y,
                        frames[i].pVertices[j].vertex.z);

                currentFrame.setVertices(temp, j);
            }

            //Check if we are past the first key frame
            if (i > 0) {
                // Here we add the current object (or frame) to our list object list
                model.addObject(currentFrame);
                continue;	// Go on to the next key frame
            }

            //We will only get here ONE because we just need this information
            // calculated for the first key frame.
            currentFrame.setNumTexcoords(header.numTexCoords);
            currentFrame.setNumFaces(header.numTriangles);

            // Go through all of the uv coordinates and assign them over to our structure.
            // The UV coordinates are not normal uv coordinates, they have a pixel ratio of
            // 0 to 256.  We want it to be a 0 to 1 ratio, so we divide the u value by the
            // skin width and the v value by the skin height.  This gives us our 0 to 1 ratio.
            // For some reason also, the v coodinate is flipped upside down.  We just subtract
            // the v coordinate from 1 to remedy this problem.
            for (j = 0; j < currentFrame.getNumTexcoords(); j++) {
                Vector temp = new Vector((float) (texCoords[j].u / (float) (header.skinWidth)),
                        (float) (1 - texCoords[j].v / (float) (header.skinHeight)), 0);
                currentFrame.setTexcoords(temp, j);
            }

            // Go through all of the face data and assign it over to OUR structure
            for (j = 0; j < currentFrame.getNumFaces(); j++) {
                currentFrame.getFace(j).setVertices(0, triangles[j].vertexIndices[0]);
                currentFrame.getFace(j).setVertices(1, triangles[j].vertexIndices[1]);
                currentFrame.getFace(j).setVertices(2, triangles[j].vertexIndices[2]);

                currentFrame.getFace(j).setTexCoords(0, triangles[j].textureIndices[0]);
                currentFrame.getFace(j).setTexCoords(1, triangles[j].textureIndices[1]);
                currentFrame.getFace(j).setTexCoords(2, triangles[j].textureIndices[2]);
            }
            currentFrame.setDimension();
            // Here we add the current object (or frame) to our list object list
            model.addObject(currentFrame);
        }
    }
}
