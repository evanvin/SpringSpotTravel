package com.spottravel.mvc;

 import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.spottravel.domain.AdventureForm;
import com.spottravel.domain.Member;
import com.spottravel.domain.Spot;
import com.spottravel.repo.MemberDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/")
public class HomeController
{
    @Autowired
    private MemberDao memberDao;
    

    @RequestMapping(method=RequestMethod.GET)
    public String showFormPage(Model model)
    {
    	model.addAttribute("interestObjects", getInterestOptions());
        model.addAttribute("adventureForm", new AdventureForm());
//        model.addAttribute("members", memberDao.findAllOrderedByName());
        return "index";
    }

    @RequestMapping(method=RequestMethod.POST)
    public String registerNewMember(@Valid @ModelAttribute("newMember") Member newMember, BindingResult result, Model model)
    {
        if (!result.hasErrors()) {
            memberDao.register(newMember);
            return "redirect:/";
        }
        else {
            model.addAttribute("members", memberDao.findAllOrderedByName());
            return "index";
        }
    }
    
    
    public String getInterestOptions(){
    	String[] tempInterests = {"amusement_park","aquarium","art_gallery","atm","bakery","bar","book_store","bowling_alley","cafe","campground","car_rental","casino","cemetery","church","city_hall","clothing_store","convenience_store","gas_station","hardware_store","hindu_temple","liquor_store","lodging","meal_takeaway","mosque","movie_theater","museum","night_club","park","parking","pet_store","restaurant","rv_park","shopping_mall","spa","stadium","store","synagogue","university","zoo"};
    	List<String> interests = new ArrayList<String>();
    	interests.add("<option value='' disabled='disabled' selected='selected'>Pit stops...</option>");
    	for(String i : tempInterests)
    		interests.add("<option value='" + i + "'>" + camalCaseString(i) + "</option>");
    	return Joiner.on("").join(interests);
    }
    
    public String camalCaseString(String y){    	
    	String x = y.replace("_", " ");
		String[] tokens = x.split("\\s");
		x = "";

		for(int j = 0; j < tokens.length; j++){
		    char capLetter = Character.toUpperCase(tokens[j].charAt(0));
		    x +=  " " + capLetter + tokens[j].substring(1);
		}
		x = x.trim();
		return x;
    }
    
    
    
    
    @RequestMapping(value="/loadMap", method=RequestMethod.POST)
    public String loadMap(@Valid @ModelAttribute("adventureForm") AdventureForm adventureForm, Model model)
    {
		
		STProcess stt = new STProcess();	
		Spot spot = stt.processDataFromForm(adventureForm);
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(spot);
			json = json.replace("\\", "\\\\");
			json = json.replace("'", "\\'");
			json = json.replace("\"", "\\\"");
			model.addAttribute("spot", json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
      
        return "test";
        
    }
    
    
    
    @RequestMapping(value="/testOne", method=RequestMethod.GET)
    public String testMethod(Model model)
    {
		STProcess stt = new STProcess();	
		Spot spot = stt.processDataTest();
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(spot);
			json = json.replace("\\", "\\\\");
			json = json.replace("'", "\\'");
			json = json.replace("\"", "\\\"");
			model.addAttribute("spot", json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
      
        return "test";
    }
}
