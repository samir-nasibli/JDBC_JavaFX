package main.util;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
	private final IntegerProperty number;
	private final StringProperty surname;
	private final StringProperty name;
	private final StringProperty city;
	private final StringProperty rank;
	private final BooleanProperty admission;
	private final ObjectProperty<LocalDate> date;
	
	public Person(PersonBuilder builder) {
		this.number = new SimpleIntegerProperty(builder.number);
		this.surname = new SimpleStringProperty(builder.surname);
		this.name = new SimpleStringProperty(builder.name);
		this.city = new SimpleStringProperty(builder.city);
		this.rank = new SimpleStringProperty(builder.rank);
		this.date = new SimpleObjectProperty<LocalDate>(builder.date);
		this.admission = new SimpleBooleanProperty(builder.admission);	
	}
	
	public void setNumber(int num) {
		this.number.set(num);
	}
	public void setSurname(String name) {
		this.name.set(name);
	}
	public void setName(String surname) {
		this.surname.set(surname);
	}
	public void setcity(String city) {
		this.city.set(city);
	}
	public void setRank(String rank) {
		this.rank.set(rank);
	}
	public void setDate(LocalDate date) {
		this.date.set(date);
	}
	public void setAdmisiion(boolean admission) {
		this.admission.set(admission);
	}
	
	public int getNumber() {
		return this.number.get();
	}
	public String getSurname() {
		return this.surname.get();
	}
	public String getName() {
		return this.name.get();
	}
	public String getCity() {
		return this.city.get();
	}
	public String getRank() {
		return this.rank.get();
	}
	public LocalDate getDate() {
		return this.date.get();
	}
	public String setStringDate() {
		return this.date.get().toString();
	}
	public boolean getAdmission() {
		return this.admission.get();
	}
	
    public IntegerProperty numberProperty() {
        return number;
    }
    public StringProperty surnameProperty() {
        return surname;
    }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty cityProperty() {
        return city;
    }
    public StringProperty rankProperty() {
        return rank;
    }
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    public BooleanProperty admissionProperty() {
        return admission;
    }

}
