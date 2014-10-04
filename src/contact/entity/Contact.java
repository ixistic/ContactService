package contact.entity;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A person is a contact with a name, title, and email. title is text to display
 * for this contact in a list of contacts, such as a nickname or company name.
 * 
 * @author jim, Veerapat Threeravipark 5510547022
 */
@Entity
@Table(name = "contact")
@XmlRootElement(name = "contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private String name;
	@XmlElement(required=true,nillable=false)
	private String title;
	private String email;
	private String phoneNumber;
	/** URL of photo */
	private String photoUrl;

	public Contact() {

	}

	public Contact(String title, String name, String email, String phoneNumber) {
		this.title = title;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.photoUrl = "";
	}

	public Contact(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photo) {
		this.photoUrl = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return String.format("[%ld] %s (%s)", id, name, title);
	}

	/**
	 * Two contacts are equal if they have the same id, even if other attributes
	 * differ.
	 * 
	 * @param other
	 *            another contact to compare to this one.
	 */
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		Contact contact = (Contact) other;
		return contact.getId() == this.getId();
	}

	/**
	 * Update this contact's data from another Contact. The id field of the
	 * update must either be 0 or the same value as this contact!
	 * 
	 * @param update
	 *            the source of update values
	 */
	public void applyUpdate(Contact update) {
		if (update == null)
			return;
		if (update.getId() != 0 && update.getId() != this.getId())
			throw new IllegalArgumentException(
					"Update contact must have same id as contact to update");
		// Since title is used to display contacts, don't allow empty title
		// if (!isEmpty(update.getTitle()))
		this.setTitle(update.getTitle()); // empty nickname is ok
		// // other attributes: allow an empty string as a way of deleting an
		// // attribute in update (this is hacky)
		// if (update.getName() != null)
		this.setName(update.getName());
		// if (update.getEmail() != null)
		this.setEmail(update.getEmail());
		// if (update.getPhoneNumber() != null)
		this.setPhoneNumber(update.getPhoneNumber());
		// if (update.getPhotoUrl() != null)
		this.setPhotoUrl(update.getPhotoUrl());
	}

	/**
	 * Test if a string is null or only whitespace.
	 * 
	 * @param arg
	 *            the string to test
	 * @return true if string variable is null or contains only whitespace
	 */
	private static boolean isEmpty(String arg) {
		return arg == null || arg.matches("\\s*");
	}
	
	public String sha1() {
		String input = ""+id+name+title+email+phoneNumber+photoUrl;
        MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }

}