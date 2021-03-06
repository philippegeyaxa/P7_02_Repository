package swa.poseidon.model;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import swa.poseidon.form.UserForm;
import swa.poseidon.form.UserFormWithPassword;

@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@Table(indexes = { @Index(name = "I_username", columnList = "username", unique=true) })
public class User implements EntityCore<UserForm>
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer userId;
    
 	@Column(length = 30)
    String username;
    
	@Column(length = 125)
    String password;

	@Column(length = 125)
    String fullname;
    
	@Column(columnDefinition = "TINYINT")
	Integer role;
    
    public User(String username, String password, String fullname, Integer role)
    {
		// userId set to 0;
		this.username=username;
		this.password=password;
		this.fullname=fullname;
		this.role=role;
    }

    public User(UserForm f)
    {
		userId=f.getUserId();
		username=f.getUsername();
		fullname=f.getFullname();
		role=f.getRole();
    }

    public User(UserFormWithPassword f)
    {
		this((UserForm) f);
		password=f.getPassword();
    }
    
    public void setPassword(String pass)
    {
    	password=pass;
    }

	@Override
	public void setId(Integer id) {
		userId = id;
	}

	@Override
	public UserForm toForm() {
		return new UserForm(this);
	}

	public UserFormWithPassword toFormWithPassword() {
		return new UserFormWithPassword(this);
	}

	@Override
	public EntityCore<UserForm> newValidTestEntityWithIdZero(int index) 
	{
		return (EntityCore<UserForm>) new User("username"+index, "Password#"+index, "fullname"+index, index*11);
	}

	@Override
	public EntityCore<UserForm> newValidTestEntityWithGivenId(int index) 
	{
		EntityCore<UserForm> ec = newValidTestEntityWithIdZero(index);
		ec.setId(index);
		return ec;
	}

	@Override
	public EntityCore<UserForm> newInvalidTestEntity() 
	{
		return (EntityCore<UserForm>) new User("", "", "", -1);
	}
}
