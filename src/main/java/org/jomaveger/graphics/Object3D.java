package org.jomaveger.graphics;

import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.jomaveger.math.Vector;
import org.jomaveger.texture.TextureCoord;
import org.jomaveger.util.BufferUtil;

/**
 * @author jmvegas.gertrudix
 */
public class Object3D {

    protected int numVertices;
    protected int numFaces;
    protected int numNormais;
    protected int numTexcoords;

    protected boolean normaisPorVertice;	// true se houver normais por vtice
    protected boolean bHasTexture; 			// true se houver materiais
    protected int materialID;
    protected int numDisplayList;				// display list, se houver

    protected ArrayList<Face> faces;
    protected ArrayList<Integer> indices;

    protected String name;

    protected Vector dimMin;
    protected Vector dimMax;
    protected Vector center;

    protected Vector[] vertices;
    protected Vector[] normal;
    protected TextureCoord[] texCoords;

    protected String drawMode;

    public final static float branco[] = {1.0f, 1.0f, 1.0f, 1.0f};	// constante para cor branca
    public final static FloatBuffer br = BufferUtil.INSTANCE.AllocFloats(branco);

    private BoundingBox boundingBox;

    public Object3D() {
        dimMin = new Vector();
        dimMax = new Vector();
        center = new Vector();
        faces = new ArrayList<>();
        setNumDisplayList(-1);
        drawMode = "t";
        materialID = -1;
    }

    public Object3D(Object3D obj) {
        this.faces = obj.faces;
        this.name = obj.name;
        this.normaisPorVertice = obj.normaisPorVertice;
        this.numDisplayList = obj.numDisplayList;
        this.numFaces = obj.numFaces;
        this.numNormais = obj.numNormais;
        this.numTexcoords = obj.numTexcoords;
        this.numVertices = obj.numVertices;
        this.dimMin = obj.dimMin;
        this.dimMax = obj.dimMax;
        this.center = obj.center;
        this.drawMode = obj.drawMode;
    }

    protected void startFaces(int total) {
        for (int i = 0; i < total; i++) {
            faces.add(new Face());
        }
    }

    public void setNumVertices(int numVertices) {
        vertices = new Vector[numVertices];
        for (int a = 0; a < numVertices; a++) {
            vertices[a] = new Vector();
        }
    }

    public void setNumVert(int numVertices) {
        this.numVertices = numVertices;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public void setNumFaces(int numFaces) {
        this.numFaces = numFaces;
        startFaces(numFaces);
    }

    public int getNumFaces() {
        return numFaces;
    }

    public void setNumNormais(int numNormais) {
        this.numNormais = numNormais;
        normal = new Vector[numNormais];
    }

    public int getNumNormais() {
        return numNormais;
    }

    public void setNumTexcoords(int numTexcoords) {
        this.numTexcoords = numTexcoords;
        texCoords = new TextureCoord[numTexcoords];

        for (int i = 0; i < texCoords.length; i++) {
            texCoords[i] = new TextureCoord();
        }
    }

    public int getNumTexcoords() {
        return numTexcoords;
    }
    
    public void setNormaisPorVertice(boolean normaisPorVertice) {
        this.normaisPorVertice = normaisPorVertice;
    }

    public boolean isNormaisPorVertice() {
        return normaisPorVertice;
    }

    public void setbHasTexture(boolean bHasTexture) {
        this.bHasTexture = bHasTexture;
    }

    public boolean isbHasTexture() {
        return bHasTexture;
    }

    public void setNumDisplayList(int numDisplayList) {
        this.numDisplayList = numDisplayList;
    }

    public int getNumDisplayList() {
        return numDisplayList;
    }

    public void setFaces(Face face) {
        this.faces.add(face);
    }

    public void setFaces(int index, Face face) {
        this.faces.set(index, face);
    }

    public void setFaces(ArrayList<Face> face) {
        this.faces = face;
    }

    public Face getFace(int index) {
        return faces.get(index);
    }

    public ArrayList<Face> getFace() {
        return faces;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDimMin(Vector dimMin) {
        this.dimMin = dimMin;
    }

    public void setDimMin(float minx, float miny, float minz) {
        this.dimMin.x = minx;
        this.dimMin.y = miny;
        this.dimMin.z = minz;
    }

    public Vector getDimMin() {
        return dimMin;
    }

    public void setDimMax(Vector dimMax) {
        this.dimMax = dimMax;
    }

    public void setDimension() {
        Vector dimMax = new Vector(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        Vector dimMin = new Vector(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

        for (int i = 0; i < getNumVertices(); i++) {
            dimMin.x = Math.min(dimMin.x, getVertices(i).x);
            dimMin.y = Math.min(dimMin.y, getVertices(i).y);
            dimMin.z = Math.min(dimMin.z, getVertices(i).z);

            dimMax.x = Math.max(dimMax.x, getVertices(i).x);
            dimMax.y = Math.max(dimMax.y, getVertices(i).y);
            dimMax.z = Math.max(dimMax.z, getVertices(i).z);
        }
        setDimMax(dimMax);
        setDimMin(dimMin);
        setCenter();
        boundingBox = new BoundingBox();
        boundingBox.createBoundingBox(dimMin, dimMax);
    }

    public void drawBoundingBox() {
        if (boundingBox != null) {
            boundingBox.drawBoundingBox();
        }
    }

    public void setDimMax(float maxx, float maxy, float maxz) {
        this.dimMax.x = maxx;
        this.dimMax.y = maxy;
        this.dimMax.z = maxz;
    }

    public Vector getDimMax() {
        return dimMax;
    }

    @Override
    public String toString() {
        return ("Nome " + name + " minx " + dimMin.x + " maxx " + dimMax.x + " miny " + dimMin.y + " maxy " + dimMax.y + " minz " + dimMin.z + " maxz " + dimMax.z);
    }

    public void setCenter() {
        Vector vec = new Vector(getDimMax());
        this.center = vec.Add(getDimMin()).Divide(2);
    }

    public Vector getCenter() {
        return center;
    }

    // Establece el modo de dibujo de los objetos
    // 'w' - wireframe
    // 's' - solido
    // 't' - solido + textura
    public void setDrawMode(String drawMode) {
        this.drawMode = drawMode;
    }

    public String getDrawMode() {
        return drawMode;
    }

    public void draw(Model3D world) {
        int ult_texid, texid;	 
        int prim = GL_POLYGON;	// tipo de primitiva

        if (drawMode.equalsIgnoreCase("w")) {
            prim = GL_LINE_LOOP;
        }

        if (getNumDisplayList() >= 1000) {
            glNewList(getNumDisplayList() - 1000, GL_COMPILE_AND_EXECUTE);
        } else if (getNumDisplayList() > -1) {
            glCallList(getNumDisplayList());
            return;
        }

        glPushAttrib(GL_LIGHTING_BIT);
        glDisable(GL_TEXTURE_2D);
      
        ult_texid = -1;
        
        for (int i = 0; i < getNumFaces(); i++) {

            if (getFace(i).getIndMat() != -1) {
                
                glDisable(GL_COLOR_MATERIAL);

                glMaterial(GL_FRONT, GL_AMBIENT, world.getMaterials(getFace(i).getIndMat()).getKd());

                if (getFace(i).getTexId() != -1 && drawMode.equalsIgnoreCase("t")) {
                    glMaterial(GL_FRONT, GL_DIFFUSE, br);
                } else {
                    glMaterial(GL_FRONT, GL_DIFFUSE, world.getMaterials(getFace(i).getIndMat()).getKd());
                    glMaterial(GL_FRONT, GL_SPECULAR, world.getMaterials(getFace(i).getIndMat()).getKs());
                    glMaterial(GL_FRONT, GL_EMISSION, world.getMaterials(getFace(i).getIndMat()).getKe());
                    glMaterialf(GL_FRONT, GL_SHININESS, world.getMaterials(getFace(i).getIndMat()).getSpec());
                }
            }

            if (getFace(i).getTexId() != -1) {
                texid = getFace(i).getTexId();
            } else {
                texid = -1;
            }

            if (texid == -1 && ult_texid != -1) {
                glDisable(GL_TEXTURE_2D);
            }
            if (texid != -1 && texid != ult_texid && drawMode.equalsIgnoreCase("t")) {
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, texid);
            }

            
            glBegin(prim);

            for (int vf = 0; vf < 3; ++vf) {
                if (getFace(i).isPerVertexNormal()) {
                    glNormal3f(getNormal(getFace(i).getNormal(vf)).x,
                            getNormal(getFace(i).getNormal(vf)).y,
                            getNormal(getFace(i).getNormal(vf)).z);
                } else {
                    glNormal3f(getNormal(i).x, getNormal(i).y, getNormal(i).z);
                }
                if (texid != -1) {
                    glTexCoord2f(getTexcoords(getFace(i).getTexCoords(vf)).s,
                            getTexcoords(getFace(i).getTexCoords(vf)).t);
                }
                glVertex3f(this.getVertices(getFace(i).getVertices(vf)).x,
                        this.getVertices(getFace(i).getVertices(vf)).y,
                        this.getVertices(getFace(i).getVertices(vf)).z);
            }
            glEnd();
            ult_texid = texid;
        }
        
        glDisable(GL_TEXTURE_2D);
        
        glPopAttrib();

        if (getNumDisplayList() >= 1000) {
            glEndList();
            setNumDisplayList(getNumDisplayList() - 1000);
        }
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setIndices(ArrayList<Integer> pIndices) {
        this.indices = pIndices;
    }

    public void setIndices(int pIndices) {
        this.indices.add(pIndices);
    }

    public void setIndices(int index, int pIndices) {
        this.indices.add(index, pIndices);
    }

    public void startIndices(int pIndices) {
        this.indices = new ArrayList<>(pIndices);
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public int getIndices(int index) {
        return indices.get(index);
    }

    public void setVertices(Vector vertices, int index) {
        this.vertices[index] = vertices;
    }

    public void setVertices(Vector[] vertices) {
        this.vertices = vertices;
    }
    
    public Vector getVertices(int index) {
        return vertices[index];
    }

    public Vector[] getVertices() {
        return vertices;
    }

    public void setNormal(Vector normal, int index) {
        this.normal[index] = normal;
    }

    public Vector getNormal(int index) {
        return normal[index];
    }

    public void setTexcoords(Vector texCoord, int index) {
        this.texCoords[index].s = texCoord.x;
        this.texCoords[index].t = texCoord.y;

    }

    public TextureCoord getTexcoords(int index) {
        return texCoords[index];
    }

    public TextureCoord[] getTexcoords() {
        return texCoords;
    }
}
