package com.spark.storeToHDFS;

import java.io.Serializable;

public class Customer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String ageNumeric;
	private String age;
	private String custID;
	private String custProfitDecline;
	private String custProfit;
	private String dob;
	private String genderDec;
	private String gender;
	private String houseHoldID;
	private String incomeNumeric;
	private String income;
	private String initials;
	private String occupationDecode;
	private String occupation;
	private String surname;
	private String telephone;
	private String title;
	private String rango;
	//private String income;
	
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getHouseHoldID() {
		return houseHoldID;
	}
	public void setHouseHoldID(String houseHoldID) {
		this.houseHoldID = houseHoldID;
	}
	public String getAgeNumeric() {
		return ageNumeric;
	}
	public void setAgeNumeric(String ageNumeric) {
		this.ageNumeric = ageNumeric;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustProfitDecline() {
		return custProfitDecline;
	}
	public void setCustProfitDecline(String custProfitDecline) {
		this.custProfitDecline = custProfitDecline;
	}
	public String getCustProfit() {
		return custProfit;
	}
	public void setCustProfit(String custProfit) {
		this.custProfit = custProfit;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getGenderDec() {
		return genderDec;
	}
	public void setGenderDec(String genderDec) {
		this.genderDec = genderDec;
	}
	public String getIncomeNumeric() {
		return incomeNumeric;
	}
	public void setIncomeNumeric(String incomeNumeric) {
		this.incomeNumeric = incomeNumeric;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
	public String getOccupationDecode() {
		return occupationDecode;
	}
	public void setOccupationDecode(String occupationDecode) {
		this.occupationDecode = occupationDecode;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRango() {
		return rango;
	}
	public void setRango(String rango) {
		this.rango = rango;
	}
	
	
}
