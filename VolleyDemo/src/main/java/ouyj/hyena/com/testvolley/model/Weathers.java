package ouyj.hyena.com.testvolley.model;

import java.util.List;

public class Weathers {

    private String success;
    private List<Result> result;


    public void setSuccess(String success){
        this.success = success;
    }
    public String getSuccess(){
        return this.success;
    }
    public void setResult(List<Result> result){
        this.result = result;
    }
    public List<Result> getResult(){
        return this.result;
    }
}
