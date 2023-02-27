package com.wynk.dto;

import com.wynk.common.Gender;
import com.wynk.common.UserType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class NdsUserInfo {

    private String location;

    private String preferredLanguage;

    private String circle;

    private UserType userType;

    private String errorCode;

    private boolean corporateUser;

    // Needed for YJ since by default it is false and user gets to see free content.
    private boolean dataUser = true;

    private Gender gender;

    private String dataRating;

    private Boolean threeGCapable;

    private Boolean gprsCapable;

    private String imei;
    
    private String firstName;
    
    private String middleName;
    
    private String lastName;
    
    private String dateOfBirth;
    
    private String emailID;
    
    private String alternateContactNumber;
    
    private String activationDate;
    
    private String networkTypeLTE;
    
    private Boolean device4gCapable;

    private String customerType;

    private String customerClassification;

    private String customerCategory;

    private String vasDND;
    
    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("location") != null) {
            setLocation((String)jsonObj.get("location"));
        }
        if(jsonObj.get("preferredLanguage") != null) {
            setPreferredLanguage((String)jsonObj.get("preferredLanguage"));
        }
        if(jsonObj.get("circle") != null) {
            setCircle((String)jsonObj.get("circle"));
        }
        if(jsonObj.get("errorCode") != null) {
            setErrorCode((String)jsonObj.get("errorCode"));
        }
        if(jsonObj.get("corporateUser") != null) {
            setCorporateUser((Boolean)jsonObj.get("corporateUser"));
        }
        if(jsonObj.get("dataUser") != null) {
            setDataUser((Boolean)jsonObj.get("dataUser"));
        }
        if(jsonObj.get("dataRating") != null) {
            setDataRating((String)jsonObj.get("dataRating"));
        }
        if(jsonObj.get("threeGCapable") != null) {
            setThreeGCapable((Boolean)jsonObj.get("threeGCapable"));
        }
        if(jsonObj.get("gprsCapable") != null) {
            setGprsCapable((Boolean)jsonObj.get("gprsCapable"));
        }
        if(jsonObj.get("imei") != null) {
            setImei((String)jsonObj.get("imei"));
        }
        if(jsonObj.get("firstName") != null) {
            setFirstName((String)jsonObj.get("firstName"));
        }
        if(jsonObj.get("middleName") != null) {
            setMiddleName((String)jsonObj.get("middleName"));
        }
        if(jsonObj.get("lastName") != null) {
            setLastName((String)jsonObj.get("lastName"));
        }
        if(jsonObj.get("dateOfBirth") != null) {
            setDateOfBirth((String)jsonObj.get("dateOfBirth"));
        }
        if(jsonObj.get("emailID") != null) {
            setEmailID((String)jsonObj.get("emailID"));
        }
        if(jsonObj.get("alternateContactNumber") != null) {
            setAlternateContactNumber((String)jsonObj.get("alternateContactNumber"));
        }
        if(jsonObj.get("activationDate") != null) {
            setActivationDate((String)jsonObj.get("activationDate"));
        }
        if(jsonObj.get("networkTypeLTE") != null) {
            setNetworkTypeLTE((String)jsonObj.get("networkTypeLTE"));
        }
        if(jsonObj.get("device4gCapable") != null) {
            setDevice4gCapable((Boolean)jsonObj.get("device4gCapable"));
        }
        if(jsonObj.get("customerType") != null) {
            setCustomerType((String) jsonObj.get("customerType"));
        }
        if(jsonObj.get("customerClassification") != null) {
            setCustomerClassification((String)jsonObj.get("customerClassification"));
        }
        if(jsonObj.get("userType") != null) {
            UserType userTyp = UserType.valueOf(((String) jsonObj.get("userType")).toUpperCase());
            setUserType(userTyp);
        }
        if(jsonObj.get("customerCategory") != null) {
            setCustomerCategory((String)jsonObj.get("customerCategory"));
        }

        if(jsonObj.get("gender") != null) {
            Gender gender = Gender.valueOf((String) jsonObj.get("gender"));
            setGender(gender);
        }

        if(jsonObj.get("vasDND") != null) {
            setVasDND((String) jsonObj.get("vasDND"));
        }
    }


    public String getVasDND() {
        return vasDND;
    }

    public void setVasDND(String vasDND) {
        this.vasDND = vasDND;
    }

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isCorporateUser() {
        return corporateUser;
    }

    public void setCorporateUser(boolean corporateUser) {
        this.corporateUser = corporateUser;
    }

    public boolean isDataUser() {
        return dataUser;
    }

    public void setDataUser(boolean dataUser) {
        this.dataUser = dataUser;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDataRating() {
        return dataRating;
    }

    public void setDataRating(String dataRating) {
        this.dataRating = dataRating;
    }

    public Boolean getThreeGCapable() {
        return threeGCapable;
    }

    public void setThreeGCapable(Boolean threeGCapable) {
        this.threeGCapable = threeGCapable;
    }

    public Boolean getGprsCapable() {
        return gprsCapable;
    }

    public void setGprsCapable(Boolean gprsCapable) {
        this.gprsCapable = gprsCapable;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
    
    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }
    
    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }
    
    public String getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}

	public String getNetworkTypeLTE() {
		return networkTypeLTE;
	}

	public void setNetworkTypeLTE(String networkTypeLTE) {
		this.networkTypeLTE = networkTypeLTE;
	}

	public Boolean getDevice4gCapable() {
		return device4gCapable;
	}

	public void setDevice4gCapable(Boolean device4gCapable) {
		this.device4gCapable = device4gCapable;
	}

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerClassification() {
        return customerClassification;
    }

    public void setCustomerClassification(String customerClassification) {
        this.customerClassification = customerClassification;
    }

    public String getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    public NdsUserInfo(String circle) {
        this.circle = circle;
    }

    public NdsUserInfo() {
    }

    @Override
    public String toString() {
        return "NdsUserInfo{" +
                "location='" + location + '\'' +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", circle='" + circle + '\'' +
                ", userType=" + userType +
                ", errorCode='" + errorCode + '\'' +
                ", corporateUser=" + corporateUser +
                ", dataUser=" + dataUser +
                ", gender=" + gender +
                ", dataRating='" + dataRating + '\'' +
                ", threeGCapable=" + threeGCapable +
                ", gprsCapable=" + gprsCapable +
                ", imei='" + imei + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", emailID='" + emailID + '\'' +
                ", alternateContactNumber='" + alternateContactNumber + '\'' +
                ", activationDate='" + activationDate + '\'' +
                ", networkTypeLTE='" + networkTypeLTE + '\'' +
                ", device4gCapable=" + device4gCapable +
                ", customerType='" + customerType + '\'' +
                ", customerClassification='" + customerClassification + '\'' +
                ", customerCategory='" + customerCategory + '\'' +
                ", vasDND='" + vasDND + '\'' +
                '}';
    }
}
