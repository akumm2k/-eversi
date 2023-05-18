package org.reversi.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wrapper for reversi disks in {@link ReversiGame}.
 */
@AllArgsConstructor
@Getter
public enum Disk {
    /**
     * White disk.
     */
    WHITE(1),
    /**
     * Black disk.
     */
    BLACK(2);
    private final int value;
}
