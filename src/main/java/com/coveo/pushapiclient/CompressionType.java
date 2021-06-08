package com.coveo.pushapiclient;

public enum CompressionType {
    UNCOMPRESSED {
        public String toString() {
            return "UNCOMPRESSED";
        }
    },
    DEFLATE {
        public String toString() {
            return "DEFLATE";
        }
    },
    GZIP {
        public String toString() {
            return "GZIP";
        }
    },
    LZMA {
        public String toString() {
            return "LZMA";
        }
    },
    ZLIB {
        public String toString() {
            return "ZLIB";
        }
    }
}