package org.reversi.mvc;

import java.io.Serializable;

/**
 * Record to represent a 2D coord
 * @param x x coord
 * @param y y coord
 */
public record Coordinate(int x, int y) implements Serializable {}