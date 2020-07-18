package com.kasa.ola.base;


public class EventCenter {
    public static class HomeSwitch {
        private int index;

        public HomeSwitch(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
    public static class TimePost {
        private long startTime = 0;
        private long endTime = 0;

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public TimePost(long startTime,long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    public static class MainBottonClickCurrent {
        private int index;

        public MainBottonClickCurrent(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
    public static class EventBean {
        private int hashCode;

        public int getHashCode() {
            return hashCode;
        }

        public void setHashCode(int hashCode) {
            this.hashCode = hashCode;
        }


    }

    public static class MapToMain{
        public MapToMain() {
        }
    }
    public static class LoginNotice{
        public LoginNotice() {
        }
    }
    public static class RefreshMyInfo{
        public RefreshMyInfo() {
        }
    }
    public static class H5Refresh {
        public H5Refresh() {
        }
    }

    public static class Repay {
        public Repay() {
        }
    }

    public static class ListRefresh {


        public ListRefresh() {

        }
    }

    public static class PwdClear {
        public PwdClear() {
        }
    }

}
