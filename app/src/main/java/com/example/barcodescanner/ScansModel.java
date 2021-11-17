package com.example.barcodescanner;

public class ScansModel {

    private String scan_res;
    private String scan_time;

    private ScansModel(){

    }
    private ScansModel(String scan_res,String scan_time){
        scan_res=this.scan_res;
        scan_time=this.scan_time;
    }
    public String getScan_res() {
        return scan_res;
    }

    public void setScan_res(String scan_res) {
        this.scan_res = scan_res;
    }

    public String getScan_time() {
        return scan_time;
    }

    public void setScan_time(String scan_time) {
        this.scan_time = scan_time;
    }


}
