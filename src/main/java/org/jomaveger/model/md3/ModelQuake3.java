package org.jomaveger.model.md3;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;

import org.lwjgl.Sys;
import org.jomaveger.graphics.AnimationInfo;
import org.jomaveger.graphics.Animations;
import org.jomaveger.graphics.Object3D;
import org.jomaveger.math.Matrix;
import org.jomaveger.math.Quaternion;
import org.jomaveger.math.Vector;
import org.jomaveger.texture.Texture;
import org.jomaveger.texture.TextureManager;
import org.jomaveger.util.BufferUtil;
import org.jomaveger.util.TextFile;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author jmvegas.gertrudix
 */
public class ModelQuake3 {

    private final static Logger LOGGER = Logger.getLogger(ModelQuake3.class);
    
    public final static int kLower = 0;			// This stores the ID for the legs model
    public final static int kUpper = 1;			// This stores the ID for the torso model
    public final static int kHead = 2;			// This stores the ID for the head model

    private final ModelMD3 head;
    private final ModelMD3 upper;
    private final ModelMD3 lower;

    // This store the players weapon model (optional load)
    private final ModelMD3 weapon;

    public TextureManager texManager;

    public ModelQuake3() {
        head = new ModelMD3();
        upper = new ModelMD3();
        lower = new ModelMD3();
        weapon = new ModelMD3();
        texManager = new TextureManager();
    }

    public ModelMD3 getModel(int whichPart) {
        // Return the legs model if desired
        if (whichPart == kLower) {
            return lower;
        }

        // Return the torso model if desired
        if (whichPart == kUpper) {
            return upper;
        }

        // Return the head model if desired
        if (whichPart == kHead) {
            return head;
        }

        // Return the weapon model
        return weapon;
    }

    ///////////////////////////////// LOAD MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This loads our Quake3 model from the given path and character name
    /////
    ///////////////////////////////// LOAD MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void load(String filePath, String fileModel) throws IOException {
        String fileLowerModel;				// This stores the file name for the lower.md3 model
        String fileUpperModel;				// This stores the file name for the upper.md3 model
        String fileHeadModel;				// This stores the file name for the head.md3  model
        String fileLowerSkin;				// This stores the file name for the lower.md3 skin
        String fileUpperSkin;				// This stores the file name for the upper.md3 skin
        String fileHeadSkin;				// This stores the file name for the head.md3  skin

        // This function is where all the character loading is taken care of.  We use
        // our ModelQuake3 class to load the 3 mesh and skins for the character. Since we
        // just have 1 name for the model, we add that to _lower.md3, _upper.md3 and _head.md3
        // to load the correct mesh files.
        // Make sure valid path and model names were passed in
        if (filePath == null || fileModel == null) {
            return;
        }

        // Store the correct files names for the .md3 and .skin file for each body part.
        // We concatinate this on top of the path name to be loaded from.
        fileLowerModel = filePath + "/" + fileModel + "_lower.md3";
        fileUpperModel = filePath + "/" + fileModel + "_upper.md3";
        fileHeadModel = filePath + "/" + fileModel + "_head.md3";

        // Get the skin file names with their path
        fileLowerSkin = filePath + "/" + fileModel + "_lower.skin";
        fileUpperSkin = filePath + "/" + fileModel + "_upper.skin";
        fileHeadSkin = filePath + "/" + fileModel + "_head.skin";

        // Next we want to load the character meshes.  The CModelMD3 class has member
        // variables for the head, upper and lower body parts.  These are of type t3DModel.
        // Depending on which model we are loading, we pass in those structures to ImportMD3.
        // This returns a true of false to let us know that the file was loaded okay.  The
        // appropriate file name to load is passed in for the last parameter.
        // Load the head mesh (*_head.md3) and make sure it loaded properly
        if (!head.load(fileHeadModel)) {
            LOGGER.info("[Error]: unable to load the HEAD part from model \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Load the upper mesh (*_head.md3) and make sure it loaded properly
        if (!upper.load(fileUpperModel)) {
            LOGGER.info("[Error]: unable to load the UPPER part from model \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Load the lower mesh (*_lower.md3) and make sure it loaded properly
        if (!lower.load(fileLowerModel)) {
            LOGGER.info("[Error]: unable to load the LOWER part from model \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Load the lower skin (*_upper.skin) and make sure it loaded properly
        if (!lower.loadSkin(fileLowerSkin)) {
            LOGGER.info("[Error]: unable to load the LOWER part from model's skin \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Load the upper skin (*_upper.skin) and make sure it loaded properly
        if (!upper.loadSkin(fileUpperSkin)) {
            LOGGER.info("[Error]: unable to load the UPPER part from model's skin \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Load the head skin (*_head.skin) and make sure it loaded properly
        if (!head.loadSkin(fileHeadSkin)) {
            LOGGER.info("[Error]: unable to load the HEAD part from model's skin \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Once the models and skins were loaded, we need to load then textures.
        // We don't do error checking for this because we call CreateTexture() and 
        // it already does error checking.  Most of the time there is only
        // one or two textures that need to be loaded for each character.  There are
        // different skins though for each character.  For instance, you could have a
        // army looking Lara Croft, or the normal look.  You can have multiple types of
        // looks for each model.  Usually it is just color changes though.
        // Load the lower, upper and head textures.  
        loadModelTextures(lower, filePath);
        loadModelTextures(upper, filePath);
        loadModelTextures(head, filePath);

////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // We added to this function the code that loads the animation config file
        // This stores the file name for the .cfg animation file
        // Add the path and file name prefix to the animation.cfg file
        String configFile = filePath + "/" + fileModel + "_animation.cfg";

        // Load the animation config file (*_animation.config) and make sure it loaded properly
        if (!loadAnimations(configFile)) {
            // Display an error message telling us the file could not be found
            LOGGER.info("[Error]: Unable to load the Animation Config File!");
            System.exit(-1);
        }

        // The character data should all be loaded when we get here (except the weapon).
        // Now comes the linking of the body parts.  This makes it so that the legs (lower.md3)
        // are the parent node, then the torso (upper.md3) is a child node of the legs.  Finally,
        // the head is a child node of the upper body.  What I mean by node, is that you can
        // think of the model having 3 bones and 2 joints.  When you translate the legs you want
        // the whole body to follow because they are inseparable (unless a magic trick goes wrong).
        // The same goes for the head, it should go wherever the body goes.  When we draw the
        // lower body, we then recursively draw all of it's children, which happen to be just the
        // upper body.  Then we draw the upper body's children, which is just the head.  So, to
        // sum this all up, to set each body part's children, we need to link them together.
        // For more information on tags, refer to the Quick Notes and the functions below.
        // Link the lower body to the upper body when the tag "tag_torso" is found in our tag array
        linkModel(lower, upper, "tag_torso");

        // Link the upper body to the head when the tag "tag_head" is found in our tag array
        linkModel(upper, head, "tag_head");

    }

    ///////////////////////////////// LOAD WEAPON \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This loads a Quake3 weapon model from the given path and weapon name
    /////
    ///////////////////////////////// LOAD WEAPON \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public final void loadWeapon(String filePath, String fileModel) throws IOException {
        String fileWeaponModel;					// This stores the file name for the weapon model
        String fileWeaponShader;					// This stores the file name for the weapon shader.

        // Make sure valid path and model names were passed in
        if (filePath == null || fileModel == null) {
            return;
        }

        // Concatenate the path and model name together
        fileWeaponModel = filePath + "/" + fileModel + ".md3";

        // Next we want to load the weapon mesh.  The CModelMD3 class has member
        // variables for the weapon model and all it's sub-objects.  This is of type t3DModel.
        // We pass in a reference to this model in to ImportMD3 to save the data read.
        // This returns a true of false to let us know that the weapon was loaded okay.  The
        // appropriate file name to load is passed in for the last parameter.
        // Load the weapon mesh (*.md3) and make sure it loaded properly
        if (!weapon.load(fileWeaponModel)) {
            LOGGER.info("[Error]: unable to load the weapon model \"" + fileModel + "\".");
            System.exit(-1);
        }

        // Unlike the other .MD3 files, a weapon has a .shader file attached with it, not a
        // .skin file.  The shader file has it's own scripting language to describe behaviors
        // of the weapon.  All we care about for this tutorial is it's normal texture maps.
        // There are other texture maps in the shader file that mention the ammo and sphere maps,
        // but we don't care about them for our purposes.  I gutted the shader file to just store
        // the texture maps.  The order these are in the file is very important.  The first
        // texture refers to the first object in the weapon mesh, the second texture refers
        // to the second object in the weapon mesh, and so on.  I didn't want to write a complex
        // .shader loader because there is a TON of things to keep track of.  It's a whole
        // scripting language for goodness sakes! :)  Keep this in mind when downloading new guns.
        // Add the path, file name and .shader extension together to get the file name and path
        fileWeaponShader = filePath + "/" + fileModel + ".shader";

        // Load our textures associated with the gun from the weapon shader file
        if (!weapon.loadShader(fileWeaponShader)) {
            LOGGER.info("[Error]: unable to load the shader for weapon \"" + fileModel + "\".");
            System.exit(-1);
        }

        // We should have the textures needed for each weapon part loaded from the weapon's
        // shader, so let's load them in the given path.
        loadModelTextures(weapon, filePath);

        // Just like when we loaded the character mesh files, we need to link the weapon to
        // our character.  The upper body mesh (upper.md3) holds a tag for the weapon.
        // This way, where ever the weapon hand moves, the gun will move with it.
        // Link the weapon to the model's hand that has the weapon tag
        linkModel(upper, weapon, "tag_weapon");

    }

    ///////////////////////////////// LOAD MODEL TEXTURES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This loads the textures for the current model passed in with a directory
    /////
    ///////////////////////////////// LOAD WEAPON \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void loadModelTextures(ModelMD3 model, String filePath) throws IOException {
        // This function loads the textures that are assigned to each mesh and it's
        // sub-objects.  For instance, the Lara Croft character has a texture for the body
        // and the face/head, and since she has the head as a sub-object in the lara_upper.md3 model, 
        // the MD3 file needs to contain texture information for each separate object in the mesh.
        // There is another thing to note too...  Some meshes use the same texture map as another 
        // one. We don't want to load 2 of the same texture maps, so we need a way to keep track of
        // which texture is already loaded so that we don't double our texture memory for no reason.
        // This is controlled with a STL vector list of "strings".  Every time we load a texture
        // we add the name of the texture to our list of strings.  Before we load another one,
        // we make sure that the texture map isn't already in our list.  If it is, we assign
        // that texture index to our current models material texture ID.  If it's a new texture,
        // then the new texture is loaded and added to our characters texture array: m_Textures[].
        // Go through all the materials that are assigned to this model
        for (int i = 0; i < model.getMaterials().size(); i++) {
            // Check to see if there is a file name to load in this material
            if (model.getMaterials(i).getName() != null) {

                String fileFullPath;

                // Add the file name and path together so we can load the texture
                fileFullPath = filePath + "/" + model.getMaterials(i).getName();

                //Texture tex = texManager.getNormalImage("texturas/" + strTexture,true, true);
                Texture tex = texManager.getFlippedImage(fileFullPath, true, false);
                //Texture tex = texManager.getMirrorImage("texturas/" + strTexture,true, true);
                // Go through all the textures in our string list to see if it's already loaded

                // Assign the texture index to our current material textureID.
                model.getMaterials(i).setTexureId(tex.getTexID());

            }

        }
    }

    ///////////////////////////////// LOAD ANIMATIONS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This loads the .cfg file that stores all the animation information
    /////
    ///////////////////////////////// LOAD ANIMATIONS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public boolean loadAnimations(String configFile) throws IOException {
        // This function is given a path and name to an animation config file to load.
        // The implementation of this function is arbitrary, so if you have a better way
        // to parse the animation file, that is just as good.  Whatever works.
        // Basically, what is happening here, is that we are grabbing an animation line:
        //
        // "0	31	0	25		// BOTH_DEATH1"
        //
        // Then parsing it's values.  The first number is the starting frame, the next
        // is the frame count for that animation (endFrame would equal startFrame + frameCount),
        // the next is the looping frames (ignored), and finally the frames per second that
        // the animation should run at.  The end of this line is the name of the animation.
        // Once we get that data, we store the information in our tAnimationInfo object, then
        // after we finish parsing the file, the animations are assigned to each model.  
        // Remember, that only the torso and the legs objects have animation.  It is important
        // to note also that the animation prefixed with BOTH_* are assigned to both the legs
        // and the torso animation list, hence the name "BOTH" :)

        // Create an animation object for every valid animation in the Quake3 Character
        AnimationInfo[] animations = new AnimationInfo[Animations.MAX_ANIMATIONS.ordinal()];

        // Open the config file
        BufferedReader reader = TextFile.openFile(configFile);

        int currentAnim = 0;				// This stores the current animation count
        int torsoOffset = 0;				// The offset between the first torso and leg animation

        // Here we go through every word in the file until a numeric number if found.
        // This is how we know that we are on the animation lines, and past header info.
        // This of course isn't the most solid way, but it works fine.  It wouldn't hurt
        // to put in some more checks to make sure no numbers are in the header info.
        String line = null;
        while ((line = reader.readLine()) != null) {

            if (line.isEmpty() || !Character.isDigit(line.charAt(0))) {
                continue;
            }

            // If we get here, we must be on an animation line, so let's parse the data.
            // We should already have the starting frame stored in strWord, so let's extract it.
            String[] linSplit = line.split("	");
            // Get the number stored in the strWord string and create some variables for the rest

            int startFrame = Integer.parseInt(linSplit[0]);
            int numOfFrames = Integer.parseInt(linSplit[1]);
            int loopingFrames = Integer.parseInt(linSplit[2]);
            int framesPerSecond = Integer.parseInt(linSplit[3]);

            // Read in the number of frames, the looping frames, then the frames per second
            // for this current animation we are on.
            // Initialize the current animation structure with the data just read in
            animations[currentAnim] = new AnimationInfo();
            animations[currentAnim].setStartFrame(startFrame);
            animations[currentAnim].setEndFrame(startFrame + numOfFrames);
            animations[currentAnim].setLoopingFrames(loopingFrames);
            animations[currentAnim].setFramesPerSecond(framesPerSecond);

            // Read past the "//" and read in the animation name (I.E. "BOTH_DEATH1").
            // This might not be how every config file is set up, so make sure.
            // Copy the name of the animation to our animation structure
            animations[currentAnim].setAnimName(linSplit[5].substring(3));
            // If the animation is for both the legs and the torso, add it to their animation list
            String animName = animations[currentAnim].getAnimName();
            if (animName.startsWith("BOTH")) {
                // Add the animation to each of the upper and lower mesh lists
                upper.addAnimations(animations[currentAnim]);
                lower.addAnimations(animations[currentAnim]);
            } // If the animation is for the torso, add it to the torso's list
            else if (animName.startsWith("TORSO")) {
                upper.addAnimations(animations[currentAnim]);
            } // If the animation is for the legs, add it to the legs's list
            else if (animName.startsWith("LEGS")) {
                // Because I found that some config files have the starting frame for the
                // torso and the legs a different number, we need to account for this by finding
                // the starting frame of the first legs animation, then subtracting the starting
                // frame of the first torso animation from it.  For some reason, some exporters
                // might keep counting up, instead of going back down to the next frame after the
                // end frame of the BOTH_DEAD3 anim.  This will make your program crash if so.

                // If the torso offset hasn't been set, set it
                if (torsoOffset == 0) {
                    //System.out.println("legs: " + animations[Animations.LEGS_WALKCR.ordinal()].getStartFrame());

                    //System.out.println("torso: " + animations[Animations.TORSO_GESTURE.ordinal()].getStartFrame());
                    torsoOffset = animations[Animations.LEGS_WALKCR.ordinal()].getStartFrame()
                            - animations[Animations.TORSO_GESTURE.ordinal()].getStartFrame();
                }

                // Minus the offset from the legs animation start and end frame.
                animations[currentAnim].setStartFrame(animations[currentAnim].getStartFrame() - torsoOffset);
                animations[currentAnim].setEndFrame(animations[currentAnim].getEndFrame() - torsoOffset);

                // Add the animation to the list of leg animations
                lower.addAnimations(animations[currentAnim]);
            }

            // Increase the current animation count
            currentAnim++;
        }

        reader.close();
        // Store the number if animations for each list by the STL vector size() function

        // Return a success
        return true;
    }

    ///////////////////////////////// LINK MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This links the body part models to each other, along with the weapon
    /////
    ///////////////////////////////// LINK MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public final void linkModel(ModelMD3 model, ModelMD3 link, String tagName) {

        // Make sure we have a valid model, link and tag name, otherwise quit this function
        if (model == null || link == null || tagName == null) {
            return;
        }

        // This function is used to link 2 models together at a pseudo joint.  For instance,
        // if we were animating an arm, we would link the top part of the arm to the shoulder,
        // then the forearm to would be linked to the top part of the arm, then the hand to
        // the forearm.  That is what is meant by linking.  That way, when we rotate the
        // arm at the shoulder, the rest of the arm will move with it because they are attached
        // to the same matrix that moves the top of the arm.  You can think of the shoulder
        // as the arm's parent node, and the rest are children that are subject to move to where
        // ever the top part of the arm goes.  That is how bone/skeletal animation works.
        //
        // So, we have an array of tags that have a position, rotation and name.  If we want
        // to link the lower body to the upper body, we would pass in the lower body mesh first,
        // then the upper body mesh, then the tag "tag_torso".  This is a tag that quake set as
        // as a standard name for the joint between the legs and the upper body.  This tag was
        // saved with the lower.md3 model.  We just need to loop through the lower body's tags,
        // and when we find "tag_torso", we link the upper.md3 mesh too that tag index in our
        // pLinks array.  This is an array of pointers to hold the address of the linked model.
        // Quake3 models are set up in a weird way, but usually you would just forget about a
        // separate array for links, you would just have a pointer to a t3DModel in the tag
        // structure, which in retrospect, you wouldn't have a tag array, you would have
        // a bone/joint array.  Stayed tuned for a bone animation tutorial from scratch.  This
        // will show you exactly what I mean if you are confused.
        // Go through all of our tags and find which tag contains the strTagName, then link'em
        for (int i = 0; i < model.getNumOfTags(); i++) {
            // If this current tag index has the tag name we are looking for
            if (model.getTags(i).nameTag.equalsIgnoreCase(tagName)) {
                // Link the model's link index to the link (or model/mesh) and return
                model.setLinks(link, i);
                return;
            }
        }

    }

    ///////////////////////////////// DRAW MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This recursively draws all the character nodes, starting with the legs
    /////
    ///////////////////////////////// DRAW MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void draw() {
        // This is the function that is called by the client (you) when using the 
        // CModelMD3 class object.  You will notice that we rotate the model by
        // -90 degrees along the x-axis.  This is because most modelers have z up
        // so we need to compensate for this.  Usually I would just switch the
        // z and y values when loading in the vertices, but the rotations that
        // are stored in the tags (joint info) are a matrix, which makes it hard
        // to change those to reflect Y up.  I didn't want to mess with that so
        // this 1 rotate will fix this problem.

        // Rotate the model to compensate for the z up orientation that the model was saved
        glRotatef(-90, 1, 0, 0);

        // You might be thinking to draw the model we would just call RenderModel()
        // 4 times for each body part and the gun right?  That sounds logical, but since
        // we are doing a bone/joint animation... and the models need to be linked together,
        // we can't do that.  It totally would ignore the tags.  Instead, we start at the
        // root model, which is the legs.  The legs drawn first, then we go through each of
        // the legs linked tags (just the upper body) and then it with the tag's rotation
        // and translation values.  I ignored the rotation in this tutorial since we aren't
        // doing any animation.  I didn't want to overwhelm you with quaternions just yet :)
        // Normally in skeletal animation, the root body part is the hip area.  Then the legs
        // bones are created as children to the torso.  The upper body is also a child to
        // the torso.  Since the legs are one whole mesh, this works out somewhat the same way.  
        // This wouldn't work if the feet and legs weren't connected in the same mesh because
        // the feet rotations and positioning don't directly effect the position and rotation
        // of the upper body, the hips do.  If that makes sense...  That is why the root starts
        // at the hips and moves down the legs, and also branches out to the upper body and
        // out to the arms.
////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // Since we have animation now, when we draw the model the animation frames need
        // to be updated.  To do that, we pass in our lower and upper models to UpdateModel().
        // There is no need to pass in the head of weapon, since they don't have any animation.
        // Update the leg and torso animations
        updateModel(lower);
        updateModel(upper);

        //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // Draw the first link, which is the lower body.  This will then recursively go
        // through the models attached to this model and drawn them.
        drawLink(lower);
    }

    ///////////////////////////////// DRAW LINK \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This draws the current mesh with an effected matrix stack from the last mesh
    /////
    ///////////////////////////////// DRAW LINK \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void drawLink(ModelMD3 model) {
        // This function is our recursive function that handles the bone animation
        // so to speak.  We first draw the model that is passed in (first the legs),
        // then go through all of it's tags and draw them.  Notice that when we
        // draw the model that is linked to the current model a new matrix scope
        // is created with glPushMatrix() and glPopMatrix().  This is because each tag
        // has a rotation and translation operation assigned to it.  For instance, when
        // Lara does her back flip death animation, the legs send a rotation and translation 
        // to the rest of the body to be rotated along with the legs as they flip backwards.  
        // If you didn't do this, Lara's body and head would stay in the same place as the
        // legs did a back flipped and landed on the floor.  Of course, this would look really
        // stupid.  A 270-degree rotation to the rest of the body is done for that animation.
        // Keep in mind, the legs mesh is NEVER translated or rotated.  It only rotates and
        // translates the upper parts of the body.  All the rotation and translation of the
        // legs is done in the canned animation that was created in the modeling program.
        // Keep in mind that I ignore the rotation value for that is given in the tag since
        // it doesn't really matter for a static model.  Also, since the rotation is given
        // in a 3x3 rotation matrix, it would be a bit more code that could make you frustrated.

        // Draw the current model passed in (Initially the legs)
        renderModel(model);

        /////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // Though the changes to this function from the previous tutorial aren't huge, they
        // are pretty powerful.  Since animation is in effect, we need to create a rotational
        // matrix for each key frame, at each joint, to be applied to the child nodes of that 
        // object.  We can also slip in the interpolated translation into that same matrix.
        // The big thing in this function is interpolating between the 2 rotations.  The process
        // involves creating 2 quaternions from the current and next key frame, then using
        // slerp (spherical linear interpolation) to find the interpolated quaternion, then
        // converting that quaternion to a 4x4 matrix, adding the interpolated translation
        // to that matrix, then finally applying it to the current model view matrix in OpenGL.
        // This will then effect the next objects that are somehow explicitly or inexplicitly
        // connected and drawn from that joint.
        // Create some local variables to store all this crazy interpolation data
        Quaternion qQuat = new Quaternion();
        Quaternion qNextQuat = new Quaternion();
        Quaternion qInterpolatedQuat;
        float[] pMatrix;
        float[] pNextMatrix;
        Matrix finalMatrix;

        //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // Now we need to go through all of this models tags and draw them.
        for (int i = 0; i < model.getNumOfTags(); i++) {
            // Get the current link from the models array of links (Pointers to models)
            ModelMD3 link = model.getLinks(i);

            // If this link has a valid address, let's draw it!
            if (link != null) {

                //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
                // To find the current translation position for this frame of animation, we times
                // the currentFrame by the number of tags, then add i.  This is similar to how
                // the vertex key frames are interpolated.
                Vector vPosition = new Vector(model.getTags(model.getCurrentFrame() * model.getNumOfTags() + i).position);

                // Grab the next key frame translation position
                Vector vNextPosition = new Vector(model.getTags(model.getNextFrame() * model.getNumOfTags() + i).position);

                // By using the equation: p(t) = p0 + t(p1 - p0), with a time t,
                // we create a new translation position that is closer to the next key frame.t
                Vector positionNew = new Vector();
                positionNew.x = vPosition.x + (model.getRatioTime() * (vNextPosition.x - vPosition.x));
                positionNew.y = vPosition.y + (model.getRatioTime() * (vNextPosition.y - vPosition.y));
                positionNew.z = vPosition.z + (model.getRatioTime() * (vNextPosition.z - vPosition.z));

                // Now comes the more complex interpolation.  Just like the translation, we
                // want to store the current and next key frame rotation matrix, then interpolate
                // between the 2.
                // Get a pointer to the start of the 3x3 rotation matrix for the current frame
                pMatrix = model.getTags(model.getCurrentFrame() * model.getNumOfTags() + i).rotation;
                Matrix ppMatrix = new Matrix(pMatrix[0], pMatrix[1], pMatrix[2], 0, pMatrix[3], pMatrix[4], pMatrix[5], 0, pMatrix[6], pMatrix[7], pMatrix[8], 0, 0, 0, 0, 1);

                // Get a pointer to the start of the 3x3 rotation matrix for the next frame
                pNextMatrix = model.getTags(model.getNextFrame() * model.getNumOfTags() + i).rotation;
                Matrix ppNextMatrix = new Matrix(pNextMatrix[0], pNextMatrix[1], pNextMatrix[2], 0, pNextMatrix[3], pNextMatrix[4], pNextMatrix[5], 0, pNextMatrix[6], pNextMatrix[7], pNextMatrix[8], 0, 0, 0, 0, 1);
                
                // Now that we have 2 1D arrays that store the matrices, let's interpolate them
                // Convert the current and next key frame 3x3 matrix into a quaternion
                qQuat.FromMatrix(ppMatrix);
                qNextQuat.FromMatrix(ppNextMatrix);

                // Using spherical linear interpolation, we find the interpolated quaternion
                qInterpolatedQuat = qQuat.slerp(model.getRatioTime(), qNextQuat);

                finalMatrix = qInterpolatedQuat.ToMatrix();

                //System.out.println("depois: "+Arrays.toString(finalMatrix.matrix));
                // To cut out the need for 2 matrix calls, we can just slip the translation
                // into the same matrix that holds the rotation.  That is what index 12-14 holds.
                finalMatrix.Tx = positionNew.x;
                finalMatrix.Ty = positionNew.y;
                finalMatrix.Tz = positionNew.z;
                
                float[] openGLMatrix = new float[16];
                buildOpenGLMatrix(openGLMatrix, finalMatrix);

                //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
                // Start a new matrix scope
                glPushMatrix();

                //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
                //glRotatef(angle, axis.x, axis.y, axis.z);
                //glTranslatef(positionNew.x, positionNew.y, positionNew.z);
                // Finally, apply the rotation and translation matrix to the current matrix
                glMultMatrix(BufferUtil.INSTANCE.AllocFloats(openGLMatrix));

                //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
                // Recursively draw the next model that is linked to the current one.
                // This could either be a body part or a gun that is attached to
                // the hand of the upper body model.
                drawLink(link);

                // End the current matrix scope
                glPopMatrix();
            }

        }

    }

    ///////////////////////////////// UPDATE MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This sets the current frame of animation, depending on it's fps and t
    /////
    ///////////////////////////////// UPDATE MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*	
    private void updateModel(ModelMD3 pModel) {
        // Initialize a start and end frame, for models with no animation
        int startFrame = 0;
        int endFrame = 1;

        // This function is used to keep track of the current and next frames of animation
        // for each model, depending on the current animation.  Some models down have animations,
        // so there won't be any change.
        // Here we grab the current animation that we are on from our model's animation list
        AnimationInfo pAnim = pModel.getAnimations(pModel.getCurrentAnim());

        // If there is any animations for this model
        if (pModel.getNumOfAnimations() > 0) {
            // Set the starting and end frame from for the current animation
            startFrame = pAnim.getStartFrame();
            endFrame = pAnim.getEndFrame();
        }
        //System.out.println(startFrame);
        //System.out.println(endFrame);

        // This gives us the next frame we are going to.  We mod the current frame plus
        // 1 by the current animations end frame to make sure the next frame is valid.
        pModel.setNextFrame((pModel.getCurrentFrame() + 1) % endFrame);

        // If the next frame is zero, that means that we need to start the animation over.
        // To do this, we set nextFrame to the starting frame of this animation.
        if (pModel.getNextFrame() == 0) {
            pModel.setNextFrame(startFrame);
        }

        // Next, we want to get the current time that we are interpolating by.  Remember,
        // if t = 0 then we are at the beginning of the animation, where if t = 1 we are at the end.
        // Anything from 0 to 1 can be thought of as a percentage from 0 to 100 percent complete.
        setCurrentTime(pModel);
    }
    ////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////

    ///////////////////////////////// SET CURRENT TIME \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This sets time t for the interpolation between the current and next key frame
    /////
    ///////////////////////////////// SET CURRENT TIME \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void setCurrentTime(ModelMD3 model) {
        float elapsedTime = 0.0f;

        // This function is very similar to finding the frames per second.
        // Instead of checking when we reach a second, we check if we reach
        // 1 second / our animation speed. (1000 ms / animationSpeed).
        // That's how we know when we need to switch to the next key frame.
        // In the process, we get the t value for how far we are at to going to the
        // next animation key frame.  We use time to do the interpolation, that way
        // it runs the same speed on any persons computer, regardless of their specs.
        // It might look choppier on a junky computer, but the key frames still be
        // changing the same time as the other persons, it will just be not as smooth
        // of a transition between each frame.  The more frames per second we get, the
        // smoother the animation will be.  Since we are working with multiple models 
        // we don't want to create static variables, so the t and elapsedTime data are 
        // stored in the model's structure.
        // Return if there is no animations in this model
        if (model.getNumOfAnimations() == 0) {
            return;
        }

        // Get the current time in milliseconds
        float time = (float) Sys.getTime();

        // Find the time that has elapsed since the last time that was stored
        elapsedTime = time - model.getLastTime();

        // Store the animation speed for this animation in a local variable
        int animationSpeed = model.getAnimations(model.getCurrentAnim()).getFramesPerSecond();

        // To find the current t we divide the elapsed time by the ratio of:
        //
        // (1_second / the_animation_frames_per_second)
        //
        // Since we are dealing with milliseconds, we need to use 1000
        // milliseconds instead of 1 because we are using GetTickCount(), which is in 
        // milliseconds. 1 second == 1000 milliseconds.  The t value is a value between 
        // 0 to 1.  It is used to tell us how far we are from the current key frame to 
        // the next key frame.
        float t = elapsedTime / (1000.0f / animationSpeed);

        // If our elapsed time goes over the desired time segment, start over and go 
        // to the next key frame.
        if (elapsedTime >= (1000.0f / animationSpeed)) {
            // Set our current frame to the next key frame (which could be the start of the anim)
            model.setCurrentFrame(model.getNextFrame());

            // Set our last time for the model to the current time
            model.setLastTime(time);
        }

        // Set the t for the model to be used in interpolation
        model.setRatioTime(t);
    }

    ///////////////////////////////// RENDER MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This renders the model data to the screen
    /////
    ///////////////////////////////// RENDER MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void renderModel(ModelMD3 model) {
        // This function actually does the rendering to OpenGL.  If you have checked out
        // our other file loading tutorials, it looks pretty much the same as those.  I
        // left out the normals though.  You can go to any other loading and copy the code
        // from those.  Usually the Quake models creating the lighting effect in their textures
        // anyway.  

        // Make sure we have valid objects just in case. (size() is in the STL vector class)
        if (model.getObject().size() <= 0) {
            return;
        }

        // Go through all of the objects stored in this model
        for (int i = 0; i < model.getObject().size(); i++) {
            // Get the current object that we are displaying
            Object3D object = model.getObject(i);

////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
            // Now that we have animation for our model, we need to interpolate between
            // the vertex key frames.  The .md3 file format stores all of the vertex 
            // key frames in a 1D array.  This means that in order to go to the next key frame,
            // we need to follow this equation:  currentFrame * numberOfVertices
            // That will give us the index of the beginning of that key frame.  We just
            // add that index to the initial face index, when indexing into the vertex array.
            // Find the current starting index for the current key frame we are on
            int currentIndex = model.getCurrentFrame() * object.getNumVertices();

            //System.out.println("current: " + currentIndex);
            // Since we are interpolating, we also need the index for the next key frame
            int nextIndex = model.getNextFrame() * object.getNumVertices();
            //System.out.println("next: " + nextIndex);

            //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
            // If the object has a texture assigned to it, let's bind it to the model.
            // This isn't really necessary since all models have textures, but I left this
            // in here to keep to the same standard as the rest of the model loaders.
            if (object.isbHasTexture()) {
                // Turn on texture mapping
                glEnable(GL_TEXTURE_2D);

                // Grab the texture index from the materialID index into our material list
                int textureID = model.getMaterials(object.getMaterialID()).getTexureId();

                // Bind the texture index that we got from the material textureID
                glBindTexture(GL_TEXTURE_2D, textureID);
            } else {
                // Turn off texture mapping
                glDisable(GL_TEXTURE_2D);
            }

            // Start drawing our model triangles
            glBegin(GL_TRIANGLES);

            // Go through all of the faces (polygons) of the object and draw them
            for (int j = 0; j < object.getNumFaces(); j++) {
                // Go through each vertex of the triangle and draw it.
                for (int whichVertex = 0; whichVertex < 3; whichVertex++) {
                    // Get the index for the current point in the face list
                    int index = object.getFace(j).getVertices(whichVertex);

                    // Make sure there is texture coordinates for this (%99.9 likelyhood)
                    if (object.getNumTexcoords() > 0) {
                        // Assign the texture coordinate to this vertex
                        glTexCoord2f(object.getTexcoords(index).s, object.getTexcoords(index).t);
                    }
                    //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////

                    // Like in the MD2 Animation tutorial, we use linear interpolation
                    // between the current and next point to find the point in between,
                    // depending on the model's "t" (0.0 to 1.0).
                    // Store the current and next frame's vertex by adding the current
                    // and next index to the initial index given from the face data.
                    //System.out.println("index: " + (currentIndex + index) );
                    //System.out.println("total: " + object.getNumVertices());
                    Vector vPoint1 = new Vector(object.getVertices(currentIndex + index));
                    Vector vPoint2 = new Vector(object.getVertices(nextIndex + index));

                    // By using the equation: p(t) = p0 + t(p1 - p0), with a time t,
                    // we create a new vertex that is closer to the next key frame.
                    glVertex3f(vPoint1.x + model.getRatioTime() * (vPoint2.x - vPoint1.x),
                            vPoint1.y + model.getRatioTime() * (vPoint2.y - vPoint1.y),
                            vPoint1.z + model.getRatioTime() * (vPoint2.z - vPoint1.z));

                    //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
                    // Get the vertex that we are dealing with.  This code will change
                    // a bunch when we doing our key frame animation in the next .MD3 tutorial.
                    //Vector3f point1 = new Vector3f(object.getVertices(index));
                    // Render the current vertex
                    //glVertex3f(point1.x, point1.y, point1.z);
                }
            }

            // Stop drawing polygons
            glEnd();
        }
    }

    ////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
    ///////////////////////////////// SET TORSO ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This sets the current animation that the upper body will be performing
    /////
    ///////////////////////////////// SET TORSO ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void setTorsoAnimation(String strAnimation) {
        // Go through all of the animations in this model
        for (int i = 0; i < upper.getNumOfAnimations(); i++) {
            // If the animation name passed in is the same as the current animation's name
            if (upper.getAnimations(i).getAnimName().equalsIgnoreCase(strAnimation)) {
                // Set the legs animation to the current animation we just found and return
                upper.setCurrentAnim(i);
                upper.setCurrentFrame(upper.getAnimations(upper.getCurrentAnim()).getStartFrame());
                break;
            }
        }
    }

    ///////////////////////////////// SET LEGS ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This sets the current animation that the lower body will be performing
    /////
    ///////////////////////////////// SET LEGS ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void setLegsAnimation(String strAnimation) {
        // Go through all of the animations in this model
        for (int i = 0; i < lower.getNumOfAnimations(); i++) {
            // If the animation name passed in is the same as the current animation's name
            if (lower.getAnimations(i).getAnimName().equalsIgnoreCase(strAnimation)) {
                // Set the legs animation to the current animation we just found and return
                lower.setCurrentAnim(i);
                lower.setCurrentFrame(lower.getAnimations(lower.getCurrentAnim()).getStartFrame());
                break;
            }
        }
    }

////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////

    private void buildOpenGLMatrix(float[] openGLMatrix, Matrix finalMatrix) {
        openGLMatrix[0] = finalMatrix.Xx;
        openGLMatrix[1] = finalMatrix.Xy;
        openGLMatrix[2] = finalMatrix.Xz;
        openGLMatrix[3] = finalMatrix.Xw;
        openGLMatrix[4] = finalMatrix.Yx;
        openGLMatrix[5] = finalMatrix.Yy;
        openGLMatrix[6] = finalMatrix.Yz;
        openGLMatrix[7] = finalMatrix.Yw;
        openGLMatrix[8] = finalMatrix.Zx;
        openGLMatrix[9] = finalMatrix.Zy;
        openGLMatrix[10] = finalMatrix.Zz;
        openGLMatrix[11] = finalMatrix.Zw;
        openGLMatrix[12] = finalMatrix.Tx;
        openGLMatrix[13] = finalMatrix.Ty;
        openGLMatrix[14] = finalMatrix.Tz;
        openGLMatrix[15] = finalMatrix.Tw;
    }
}
