package com.kasa.ola.bean.entity;

import java.util.List;

public class SkuBean {


    /**
     * speTitle : 尺码
     * speItem : [{"speID":56,"speName":"39","speImgUrl":""},{"speID":57,"speName":"40","speImgUrl":""},{"speID":58,"speName":"41","speImgUrl":""},{"speID":59,"speName":"42","speImgUrl":""},{"speID":60,"speName":"43","speImgUrl":""},{"speID":61,"speName":"44","speImgUrl":""}]
     */

    private String speTitle;
    private List<SpeItemBean> speItem;

    public String getSpeTitle() {
        return speTitle;
    }

    public void setSpeTitle(String speTitle) {
        this.speTitle = speTitle;
    }

    public List<SpeItemBean> getSpeItem() {
        return speItem;
    }

    public void setSpeItem(List<SpeItemBean> speItem) {
        this.speItem = speItem;
    }

    public static class SpeItemBean {
        /**
         * speID : 56
         * speName : 39
         * speImgUrl :
         */

        private int speID;
        private String speName;
        private String speImgUrl;
        private boolean selected = false;
        private boolean isClickable = true;
        public boolean isClickable() {
            return isClickable;
        }

        public void setClickable(boolean clickable) {
            isClickable = clickable;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public int getSpeID() {
            return speID;
        }

        public void setSpeID(int speID) {
            this.speID = speID;
        }

        public String getSpeName() {
            return speName;
        }

        public void setSpeName(String speName) {
            this.speName = speName;
        }

        public String getSpeImgUrl() {
            return speImgUrl;
        }

        public void setSpeImgUrl(String speImgUrl) {
            this.speImgUrl = speImgUrl;
        }
    }
}
