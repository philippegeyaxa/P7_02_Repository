package swa.poseidon.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import swa.poseidon.form.BidForm;
import swa.poseidon.model.Bid;
import swa.poseidon.repositories.BidRepository;
import javax.annotation.PostConstruct;

@SpringBootTest
public class BidServiceTest extends EntityServiceTest<Bid,BidForm>
{
	@MockBean
	private BidRepository autowiredRepository;
	
	@Autowired
	private BidService autowiredService;
	
	@PostConstruct
	public void injectEntityProperties()
	{
		super.entityRepository = autowiredRepository;
		super.entityService = autowiredService;
		super.entity = new Bid();
		super.entityCore = entity;
	}
}
