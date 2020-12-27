package com.moon.jachisekki.refrigerator;

//Recipe 정보 저장 클래스
public class ContentDTO  {
    //정보 저장 변수
    public String uId;
    public String rHow;
    public String rIngrediants;
    public String rName;
    public String rPic;
    public String rTime;
    public int rCount;

    public ContentDTO(String uId, String rName, String rIngrediants, String rHow, String rTime, String rPic, int rCount){
        this.uId = uId;
        this.rName = rName;
        this.rIngrediants = rIngrediants;
        this.rHow = rHow;
        this.rTime = rTime;
        this.rPic = rPic;
        this.rCount = rCount;
    }
    //getter 함수
    public String getrHow(){
        return  rHow;
}
    public String getrIngrediants(){
        return  rIngrediants;
    }
    public String getRname(){
        return  rName;
    }
    public String getrTime(){
        return  rTime;
    }
    public  String getrPic(){
        return  rPic;
    }
    public String getuId(){return uId;}
    public int getrCount() {
        return rCount;
    }

}
