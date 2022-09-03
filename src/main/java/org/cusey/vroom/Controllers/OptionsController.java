package org.cusey.vroom.Controllers;

/**
 * The Options Controller implements an application that
 *
 *
 * @author  Cusey,John
 * @version 1.0
 * @since   2018-06-08
 */

import java.util.List;

import org.cusey.vroom.Models.Dictionary;
import org.cusey.vroom.Services.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.cusey.vroom.Models.SettingsBO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class OptionsController {

    private final static Logger slf4jLogger = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    private SettingsBO settings;
    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value= "/options",  method = RequestMethod.GET)

    public String getIndex( Model model ){

        slf4jLogger.debug("getIndex");
        model.addAttribute("settings", this.settings);

        return "options";

    }

    @RequestMapping(value= "/options", method = RequestMethod.POST, params={"uploadDatabase"})
    public String uploadDatabase(@ModelAttribute SettingsBO settings, BindingResult bindingResult, Model model){

        slf4jLogger.debug("uploadDatabase");
        if (bindingResult.hasErrors()) {
            //errors processing
        }
        model.addAttribute("settings", settings);

        List<Dictionary> dicList = settings.populateDictionary();

        for(Dictionary element: dicList){
            dictionaryService.save(element);
        }
        
        settings.setSuccessMessage(("Total Number of entries uploaded are " + dicList.size()));

        return "options";

    }

    @RequestMapping(value= "/options", method = RequestMethod.POST, params={"downloadDatabase"})
    public String downloadDatabase(@ModelAttribute SettingsBO settings, BindingResult bindingResult, Model model){

        slf4jLogger.debug("downloadDatabase");
        if (bindingResult.hasErrors()) {
            //errors processing
        }
        model.addAttribute("settings", settings);
        
        List<Dictionary> dicList = dictionaryService.findAll();
        
        int rowCount =  settings.populateFlatFile(dicList);

        settings.setSuccessMessage(("Total Number of entries printed are " + rowCount + " rows."));


        return "options";

    }

    @RequestMapping(value= "/options", method = RequestMethod.POST, params={"deleteDatabase"})
    public String deleteDatabase(@ModelAttribute SettingsBO settings, BindingResult bindingResult, Model model){

        slf4jLogger.debug("deleteDatabase");
        if (bindingResult.hasErrors()) {
            //errors processing
        }
        model.addAttribute("settings", settings);

        dictionaryService.deleteAll();
        
        settings.setDangerMessage(("You have delete all the contains of the database."));

        return "options";

    }
}
