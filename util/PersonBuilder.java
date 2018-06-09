package main.util;

import java.time.LocalDate;


public class PersonBuilder {
	public  int number;
	public  String surname;
	public  String name;
	public  String city;
	public  String rank;
	public  Boolean admission;
	public  LocalDate date;
	
	public PersonBuilder(int num, String surname) {
		this.number = num;
		this.surname = surname;
		this.name = "";
		this.city = "";
		this.rank = "";
		this.date = LocalDate.now();
		this.admission = false;
	}
	
	public PersonBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public PersonBuilder setCity(String city) {
		this.city = city;
		return this;
	}
	
	public PersonBuilder setRank(Rank rank) {
		this.rank = rank.toString();
		return this;
	}
	public PersonBuilder setRank(String rank) {
		this.rank = rank;
		return this;
	}
	
	public PersonBuilder setDate(String date) {
		this.date = LocalDate.parse(date);
		return this;
	}
	public PersonBuilder setDate(LocalDate value) {
		this.date = value;
		return this;
	}
	
	public PersonBuilder setAddmision(String admission) {
		this.admission = Boolean.parseBoolean(admission);
		return this;
	}
	public PersonBuilder setAddmision(boolean admission) {
		this.admission = admission;
		return this;
	}
	
	public Person build() {
		return new Person(this);
	}
}
