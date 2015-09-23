package com.example.gordon.nthuais;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gordon on 9/23/15.
 */
public class Course {
    private int id;
    private String code;
    private String room;
    private String no;
    private int credit;
    private String time_token;
    private String eng_title;
    private String chi_title;
    private String teacher;
    private String note;
    private String objective;
    private String ge;
    private String time;
    private boolean prerequisite;

    public Course() {}

    public Course(JSONObject object) throws JSONException {
        id = object.getInt("id");
        code = object.getString("code");
        room = object.getString("room");
        no = object.getString("no");
        credit = object.getInt("credit");
        time_token = object.getString("time_token");
        eng_title = object.getString("eng_title");
        chi_title = object.getString("chi_title");
        teacher = object.getString("teacher");
        note = object.getString("note");
        objective = object.getString("objective");
        ge = object.getString("ge");
        time = object.getString("time");
	    prerequisite = object.getBoolean("prerequisite");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getTime_token() {
        return time_token;
    }

    public void setTime_token(String time_token) {
        this.time_token = time_token;
    }

    public String getEng_title() {
        return eng_title;
    }

    public void setEng_title(String eng_title) {
        this.eng_title = eng_title;
    }

    public String getChi_title() {
        return chi_title;
    }

    public void setChi_title(String chi_title) {
        this.chi_title = chi_title;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getGe() {
        return ge;
    }

    public void setGe(String ge) {
        this.ge = ge;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(boolean prerequisite) {
        this.prerequisite = prerequisite;
    }
}
