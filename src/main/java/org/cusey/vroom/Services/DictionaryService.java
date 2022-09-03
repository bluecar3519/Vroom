package org.cusey.vroom.Services;

import org.cusey.vroom.Dao.DictionaryRepository;
import org.cusey.vroom.Models.Dictionary;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository =dictionaryRepository;
    }

    public List<Dictionary> findAll(){
        List<Dictionary>  dictionaryList = new ArrayList<>();

        for (Dictionary dictionary : dictionaryRepository.findAll()){
            dictionaryList.add(dictionary);
        }

        return dictionaryList;
    }

    public void save(Dictionary dictionary){
        dictionaryRepository.save(dictionary);
    }

    public void delete(int id){
        dictionaryRepository.deleteById(id);
    }

    public void deleteAll(){
        dictionaryRepository.deleteAll();
    }


}
