package swa.poseidon.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import swa.poseidon.controllers.ExceptionManager;
import swa.poseidon.form.FormCore;
import swa.poseidon.model.EntityCore;

public abstract class EntityControllerIT<E,F>
{
	protected MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	protected JpaRepository<E,Integer> entityRepository;
	protected E entity;
	protected EntityCore<F> entityCore;
	protected String entityRootRequestMapping;
	protected int numberOfEntityFieldsToValidate;
	
	@SuppressWarnings("unchecked")
	private E saveNewTestEntityToRepository(int index)
	{
		return entityRepository.save((E)entityCore.newValidTestEntityWithGivenId(index));
	}

	@BeforeEach
	public void cleanEntityInDatabase()
	{
		entityRepository.deleteAll();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenEntityList_readAll_returnsCorrectList() throws Exception 
	{
		// GIVEN
		E e1 =  saveNewTestEntityToRepository(1);
		E e2 =  saveNewTestEntityToRepository(2);
		E e3 =  saveNewTestEntityToRepository(3);
		// WHEN
		String responseString = mvc.perform(get(entityRootRequestMapping+"/list")).andDo(print()).andReturn().getResponse().getContentAsString();
		JavaType expectedResultType = objectMapper.getTypeFactory().constructCollectionType(List.class, entityCore.toForm().getClass());
		List<F> responseObject = objectMapper.readValue(responseString, expectedResultType);
		// THEN
		assertNotNull(responseObject); 
		assertEquals(3, responseObject.size());
		FormCore<E> f1 = (FormCore<E>) responseObject.get(0);
		assertTrue(f1.matches(e1));
		FormCore<E> f2 = (FormCore<E>) responseObject.get(1);
		assertTrue(f2.matches(e2));
		FormCore<E> f3 = (FormCore<E>) responseObject.get(2);
		assertTrue(f3.matches(e3));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void givenValidForm_post_returnsCreatedEntity() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity = (EntityCore<F>) entityCore.newValidTestEntityWithIdZero(1);
		F givenForm = givenEntity.toForm();
		EntityCore<F> expectedEntity = (EntityCore<F>) entityCore.newValidTestEntityWithIdZero(1);
		String json = objectMapper.writeValueAsString(givenForm);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(entityRootRequestMapping+"/add")
			.contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON);
		// WHEN & THEN
		String responseString = mvc.perform(builder).andDo(print())
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		Object responseObject = objectMapper.readValue(responseString, givenForm.getClass());
		assertNotNull(responseObject); 
		FormCore<E> resultForm = (FormCore<E>) responseObject;
		expectedEntity.setId(resultForm.id());
		assertTrue(resultForm.matches((E)expectedEntity));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void givenInvalidForm_post_returnsBadRequestStatusAndMessages() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity = (EntityCore<F>) entityCore.newInvalidTestEntity();
		F givenForm = givenEntity.toForm();
		String json = objectMapper.writeValueAsString(givenForm);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(entityRootRequestMapping+"/add")
			.contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON);
		// WHEN & THEN
		String responseString = mvc.perform(builder).andDo(print())
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString();
		ArrayList<String> responseObject = objectMapper.readValue(responseString, ArrayList.class);
		assertNotNull(responseObject); 
		assertEquals(numberOfEntityFieldsToValidate, responseObject.size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void givenCorrectForm_put_returnsUpdatedEntity() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity =  (EntityCore<F>) saveNewTestEntityToRepository(1);
		FormCore<E> givenForm = (FormCore<E>) givenEntity.toForm();
		Integer id = givenForm.id();
		String json = objectMapper.writeValueAsString((F) givenForm);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(entityRootRequestMapping+"/update/" + id)
			.contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON);
		// WHEN & THEN
		String responseString = mvc.perform(builder).andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		Object responseObject = objectMapper.readValue(responseString, givenForm.getClass());
		assertNotNull(responseObject); 
		FormCore<E> resultForm = (FormCore<E>) responseObject;
		assertTrue(resultForm.matches((E)givenEntity));
	}
	
	@Test
	public void givenInvalidId_put_throwsNoSuchElementException() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity = (EntityCore<F>) entityCore.newValidTestEntityWithGivenId(1);
		F givenForm = givenEntity.toForm();
		String json = objectMapper.writeValueAsString(givenForm);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(entityRootRequestMapping+"/update/" + 1)
			.contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON);
		// WHEN
		String responseString = mvc.perform(builder).andDo(print()).andReturn().getResponse().getContentAsString();
		// THEN
		assertEquals(ExceptionManager.MESSAGE_NOT_SUCH_ELEMENT, responseString);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void givenValidId_read_returnsCorrectForm() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity =  (EntityCore<F>) saveNewTestEntityToRepository(1);
		FormCore<E> givenForm = (FormCore<E>) givenEntity.toForm();
		Integer id = givenForm.id();
		// WHEN & THEN
		String responseString = mvc.perform(get(entityRootRequestMapping+"/read/"+id)).andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		Object responseObject = objectMapper.readValue(responseString, givenForm.getClass());
		assertNotNull(responseObject); 
		FormCore<E> resultForm = (FormCore<E>) responseObject;
		assertTrue(resultForm.matches((E)givenEntity));
	}	

	@Test
	public void givenWrongId_read_throwsNoSuchElementException() throws Exception 
	{
		// GIVEN
		Integer id = 999;
		// WHEN 
		String responseString = mvc.perform(get(entityRootRequestMapping+"/read/"+id))
				.andDo(print()).andReturn().getResponse().getContentAsString();
		// THEN
		assertEquals(ExceptionManager.MESSAGE_NOT_SUCH_ELEMENT, responseString);
	}	

	@Test
	@SuppressWarnings("unchecked")
	public void givenValidId_delete_removesEntity() throws Exception 
	{
		// GIVEN
		EntityCore<F> givenEntity =  (EntityCore<F>) saveNewTestEntityToRepository(1);
		FormCore<E> givenForm = (FormCore<E>) givenEntity.toForm();
		Integer id = givenForm.id();
		// WHEN
		String responseString = mvc.perform(delete(entityRootRequestMapping+"/delete/"+id))
				.andDo(print()).andReturn().getResponse().getContentAsString();
		Boolean responseObject = objectMapper.readValue(responseString, Boolean.class);
		// THEN
		assertTrue(responseObject);
	}

	@Test
	public void givenWrongId_delete_returnsNoSuchElementMessage() throws Exception 
	{
		// GIVEN
		Integer id = 999;
		// WHEN 
		String responseString = mvc.perform(delete(entityRootRequestMapping+"/delete/"+id))
				.andDo(print()).andReturn().getResponse().getContentAsString();
		// THEN
		assertEquals(ExceptionManager.MESSAGE_NOT_SUCH_ELEMENT, responseString);
	}	
}
