package com.toetracker.fitnesstrainer;

/**
 * Created by rajmarappan on 10/25/15.
 */
public class ExcerciseData {

    String id,name,unit1,unit2,unit3;
    public ExcerciseData(String id, String name){
        this.setId(id);
        this.setName(name);
    }
    public ExcerciseData(String id, String name, String unit1, String unit2, String unit3){
        this.setId(id);
        this.setName(name);
        this.setUnit1(unit1);
        this.setUnit2(unit2);
        this.setUnit3(unit3);
    }
    public String getId() {  return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit1(){return  unit1;}
    public void setUnit1(String unit1){this.unit1=unit1;}

    public String getUnit2(){return  unit2;}
    public void setUnit2(String unit2){this.unit2=unit2;}

    public String getUnit3(){return  unit3;}
    public void setUnit3(String unit3){this.unit3=unit3;}


}
