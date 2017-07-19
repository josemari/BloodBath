package org.jomaveger.model.bsp;

// This is our bitset class for storing which face has already been drawn.
// The bitset functionality isn't really taken advantage of in this version
// since we aren't rendering by leafs and nodes.
/**
 * @author jmvegas.gertrudix
 */
public class BitSet {

    // Our private bit data that holds the bits and size
    private int[] m_bits;
    private int m_size;

    public BitSet() {
        m_bits = null;
        m_size = 0;
    }

    // This resizes our bitset to a size so each face has a bit associated with it
    public void resize(int count) {
        // Get the size of integers we need
        m_size = (count - 1) / 32 + 1;

        // Make sure we haven't already allocated memory for the bits
        if (m_bits != null) {
            if (m_bits.length > 0) {
                m_bits = null;
            }
        }

        // Allocate the bits and initialize them
        m_bits = new int[m_size];
        clearAll();
    }

    // This does the binary math to set the desired bit
    public void set(int i) {
        m_bits[i >> 5] |= (1 << (i & 31));
    }

    // This returns if the desired bit slot is a 1 or a 0
    public boolean on(int i) {
        return (m_bits[i >> 5] & (1 << (i & 31))) == 0;
    }

    // This clears a bit to 0
    public void clear(int i) {
        m_bits[i >> 5] = m_bits[i >> 5] & ~(1 << (i & 31));
    }

    // This initializes the bits to 0
    public void clearAll() {
        for (int i = 0; i < m_bits.length; i++) {
            m_bits[i] = 0;
        }
    }
}
