package org.cusey.vroom.Controllers;

import org.cusey.vroom.Models.Dictionary;
import org.cusey.vroom.Services.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class EditController {
    private final static Logger slf4jLogger = LoggerFactory.getLogger(EditController.class);

    @Autowired
    private DictionaryService dictionaryService;


    //http://localhost:8097/edit
    @RequestMapping(value = "/edit" , method = RequestMethod.GET)
    public String showEdit(HttpServletRequest request){
        slf4jLogger.debug("showEdit");

        List<Dictionary> dictionaryList = dictionaryService.findAll();

        request.setAttribute("Dictionary", dictionaryList);
        return "edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public int entitySave(@RequestBody Dictionary dictionary) {
        slf4jLogger.debug("entitySave");

        dictionaryService.save(dictionary);

        return dictionary.getId();

    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void entityDelete(@PathVariable(value="id") int idDelete) {
        slf4jLogger.debug("entityDelete");

        dictionaryService.delete(idDelete);


    }
}
