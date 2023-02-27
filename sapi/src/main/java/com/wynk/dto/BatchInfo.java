package com.wynk.dto;

import org.bson.types.ObjectId;

public class BatchInfo {

        private int batchSize;
        private int batchNum;
        private ObjectId startObjectId;
        private ObjectId endObjectId;

        public BatchInfo(int batchSize, int batchNum, ObjectId startObjectId, ObjectId endObjectId) {
            this.batchSize = batchSize;
            this.batchNum = batchNum;
            this.startObjectId = startObjectId;
            this.endObjectId = endObjectId;
        }

        public int getBatchSize() {
            return batchSize;
        }

        void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getBatchNum() {
            return batchNum;
        }

        void setBatchNum(int batchNum) {
            this.batchNum = batchNum;
        }

        public ObjectId getStartObjectId() {
            return startObjectId;
        }

        void setStartObjectId(ObjectId startObjectId) {
            this.startObjectId = startObjectId;
        }

        public ObjectId getEndObjectId() {
            return endObjectId;
        }

        void setEndObjectId(ObjectId endObjectId) {
            this.endObjectId = endObjectId;
        }
    }