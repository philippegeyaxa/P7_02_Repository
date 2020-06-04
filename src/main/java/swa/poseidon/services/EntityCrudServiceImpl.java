package swa.poseidon.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class EntityCrudServiceImpl<E,F> implements EntityCrudService<E,F>
{
	@Autowired JpaRepository<E, Integer> repository;
	
	@Override
	public List<F> readAll()
	{
		List<E> eList = repository.findAll();
		List<F> fList = new ArrayList<F>();
		for (E e : eList) 
		{
			@SuppressWarnings("unchecked")
			EntityModelService<F> ems = (EntityModelService<F>) e;
			F f = ems.newForm();
			fList.add(f);
		}
		return fList;
	}

	@Override
	public E create(E e) 
	{
		@SuppressWarnings("unchecked")
		EntityModelService<F> ems = (EntityModelService<F>) e;
		ems.setId(0);
		return repository.save(e);
	}

	@Override
	public E read(Integer eId) 
	{
		Optional<E> e = repository.findById(eId);
		if (e == null) return null;
		else return e.get();
	}

	@Override
	public E update(E e) 
	{
		return repository.save(e);
	}

	@Override
	public boolean delete(Integer eId) {
		E e = read(eId);
		if (e == null) return false;
		repository.delete(e);
		return true;
	}
}
