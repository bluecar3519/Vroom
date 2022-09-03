package org.cusey.vroom.Controllers;

import org.cusey.vroom.Models.Dictionary;
import org.cusey.vroom.Services.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    private final static Logger slf4jLogger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private DictionaryService dictionaryService;

    //http://localhost:8097/
    @RequestMapping("/")
    public String showIndex(HttpServletRequest request){

        slf4jLogger.debug("showIndex");

        List<Dictionary> dictionaryList = dictionaryService.findAll();
        request.setAttribute("Dictionary", dictionaryList);


        return "index";
    }


}
