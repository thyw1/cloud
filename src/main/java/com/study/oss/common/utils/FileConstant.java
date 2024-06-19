package com.study.oss.common.utils;

/**
 * @author john
 * @date 2020-01-11
 */
public class FileConstant{
    public enum FileType {
        /**
         * 文件类型
         */
        DEFAULT(0,"文件"),
        FOLDER(1,"文件夹");
//        VIDEO,
//        AUDIO,
//        PDF,
//        COMPRESS_FILE,
//        PICTURE,
//        DOC,
//        PPT,
//        TXT,
//        TORRENT,
//        WEB,
//        CODE
//        ;

        private int code;
        private String msg;

        FileType(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}

