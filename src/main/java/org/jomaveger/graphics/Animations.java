package org.jomaveger.graphics;

/**
 * @author jmvegas.gertrudix
 */
public enum Animations {

    // If one model is set to one of the BOTH_* animations, the other one should be too,
    // otherwise it looks really bad and confusing.
    BOTH_DEATH1, // The first twirling death animation
    BOTH_DEAD1, // The end of the first twirling death animation
    BOTH_DEATH2, // The second twirling death animation
    BOTH_DEAD2, // The end of the second twirling death animation
    BOTH_DEATH3, // The back flip death animation
    BOTH_DEAD3, // The end of the back flip death animation

    // The next block is the animations that the upper body performs

    TORSO_GESTURE, // The torso's gesturing animation

    TORSO_ATTACK, // The torso's attack1 animation
    TORSO_ATTACK2, // The torso's attack2 animation

    TORSO_DROP, // The torso's weapon drop animation
    TORSO_RAISE, // The torso's weapon pickup animation

    TORSO_STAND, // The torso's idle stand animation
    TORSO_STAND2, // The torso's idle stand2 animation

    // The final block is the animations that the legs perform

    LEGS_WALKCR, // The legs's crouching walk animation
    LEGS_WALK, // The legs's walk animation
    LEGS_RUN, // The legs's run animation
    LEGS_BACK, // The legs's running backwards animation
    LEGS_SWIM, // The legs's swimming animation

    LEGS_JUMP, // The legs's jumping animation
    LEGS_LAND, // The legs's landing animation

    LEGS_JUMPB, // The legs's jumping back animation
    LEGS_LANDB, // The legs's landing back animation

    LEGS_IDLE, // The legs's idle stand animation
    LEGS_IDLECR, // The legs's idle crouching animation

    LEGS_TURN, // The legs's turn animation

    MAX_ANIMATIONS			// The define for the maximum amount of animations

}
