package jana60.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jana60.model.Pizza;
import jana60.repository.PizzaRepository;

@Controller
@RequestMapping("/")
public class PizzaController {

	@Autowired
	private PizzaRepository repo;
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@GetMapping("/pizza")
	public String pizza(Model model) {
		
		List<Pizza> pizzaList = (List<Pizza>) repo.findAll();
	    
		model.addAttribute("pizzaList", pizzaList);
		return "pizza";
	    
	}
	
	
}
