package com.coveo.pushapiclient;

public enum SourceVisibility {
    PRIVATE {
        public String toString() {
            return "PRIVATE";
        }
    },
    SECURED {
        public String toString() {
            return "SECURED";
        }
    },
    SHARED {
        public String toString() {
            return "SHARED";
        }
    }
}
