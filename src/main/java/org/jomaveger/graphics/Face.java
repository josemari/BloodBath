package org.jomaveger.graphics;

/**
 * @author jmvegas.gertrudix
 */
public class Face {

    private int[] vertices;
    private int[] normal;
    private int[] texCoords;
    private int indMat;		
    private int texId;	
    private boolean perVertexNormal;

    public Face() {
        setVertices(new int[3]);
        setNormal(new int[3]);
        setTexCoords(new int[3]);
        indMat = -1;
        texId = -1;
    }

    public void setVertices(int[] vertices) {
        this.vertices = vertices;
    }

    public void setVertices(int indexVec, int index) {
        this.vertices[indexVec] = index;
    }

    public int[] getVertices() {
        return vertices;
    }

    public int getVertices(int index) {
        return vertices[index];
    }

    public void setNormal(int[] normal) {
        this.normal = normal;
    }

    public void setNormal(int indexVec, int index) {
        this.normal[indexVec] = index;
    }

    public int[] getNormal() {
        return normal;
    }

    public int getNormal(int index) {
        return normal[index];
    }

    public void setTexCoords(int[] texCoords) {
        this.texCoords = texCoords;
    }

    public void setTexCoords(int indexVec, int index) {
        this.texCoords[indexVec] = index;
    }

    public int[] getTexCoords() {
        return texCoords;
    }

    public int getTexCoords(int index) {
        return texCoords[index];
    }

    public void setIndMat(int indMat) {
        this.indMat = indMat;
    }

    public int getIndMat() {
        return indMat;
    }

    public void setTexId(int texId) {
        this.texId = texId;
    }

    public int getTexId() {
        return texId;
    }

    public void setPerVertexNormal(boolean perVertexNormal) {
        this.perVertexNormal = perVertexNormal;
    }

    public boolean isPerVertexNormal() {
        return perVertexNormal;
    }
}
