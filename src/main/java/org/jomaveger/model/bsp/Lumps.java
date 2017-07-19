package org.jomaveger.model.bsp;

/**
 * @author jmvegas.gertrudix
 */
public enum Lumps {

    kEntities, // Stores player/object positions, etc...
    kTextures, // Stores texture information
    kPlanes, // Stores the splitting planes
    kNodes, // Stores the BSP nodes
    kLeafs, // Stores the leafs of the nodes
    kLeafFaces, // Stores the leaf's indices into the faces
    kLeafBrushes, // Stores the leaf's indices into the brushes
    kModels, // Stores the info of world models
    kBrushes, // Stores the brushes info (for collision)
    kBrushSides, // Stores the brush surfaces info
    kVertices, // Stores the level vertices
    kIndices, // Stores the level indices
    kShaders, // Stores the shader files (blending, anims..)
    kFaces, // Stores the faces for the level
    kLightmaps, // Stores the lightmaps for the level
    kLightVolumes, // Stores extra world lighting information
    kVisData, // Stores PVS and cluster info (visibility)
    kMaxLumps					// A constant to store the number of lumps
}
