package com.coveo.pushapiclient;

/**
 * Enum for possible PushAPI statuses.
 *
 * @see <a href="https://docs.coveo.com/en/35">Updating the Status of a Push Source</a>
 */
public enum PushAPIStatus {
  IDLE,
  REBUILD,
  INCREMENTAL,
  REFRESH;
}
