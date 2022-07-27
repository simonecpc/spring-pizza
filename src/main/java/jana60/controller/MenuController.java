package jana60.controller;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Optional;

import javax.swing.text.AbstractDocument.BranchElement;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jana60.model.Pizza;
import jana60.repository.PizzaRepository;

@Controller
@RequestMapping("/pizza")
public class MenuController {

	@Autowired
	public PizzaRepository repoPizza;
	
	@GetMapping
	public String menu(Model model) {
		
		List<Pizza> pizzaList = (List<Pizza>) repoPizza.findAllByOrderByPrezzoAsc();
		model.addAttribute("pizzaList", pizzaList);
		
		return "pizza";
		
	}
	
	@GetMapping("/aggiungi")
	public String formPizza(Model model) {
		
		model.addAttribute("pizza", new Pizza());
		
		return "aggiungi";
		
	}
	
	@GetMapping("/modifica/{pizzaId}")
	public String modifica(@PathVariable(name = "pizzaId") Integer pizzaId, Model model) {
		
		Optional<Pizza> selezionaId = repoPizza.findById(pizzaId);
		
		if(selezionaId.isPresent()) {
			
			model.addAttribute("pizza", selezionaId.get());
			return "modifica";
			
		} else {
			
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pizza cercata non esiste");
			
		}
		
	}
	
	@PostMapping("/salva")
	public String salva(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult br ,Model model) {
		
		boolean hasErrors = br.hasErrors();
		boolean validaNome = true;
		boolean nuovaPizza = true;
				
		if(formPizza.getId() != null) {
			
			nuovaPizza = false;
			
			Pizza pizzaPrecedenteAllaModifica = repoPizza.findById(formPizza.getId()).get();
			
			if(pizzaPrecedenteAllaModifica.getNome().equalsIgnoreCase(formPizza.getNome())) {
				
				validaNome = false;
				
			}
			
		}
		
		if(validaNome && repoPizza.countByNome(formPizza.getNome()) > 0) {
			
			br.addError(new FieldError("pizza", "nome", "Il nome deve essere unico"));
			
			hasErrors = true;
			
		}
		
		if(hasErrors)
			if(nuovaPizza)
				return "/aggiungi";
			else
				return "/modifica";
		else {
			
			try {
				
				repoPizza.save(formPizza);
				
			} catch(Exception e) {
				
				model.addAttribute("errorMessage", "Impossibile salvare le modifiche");
				
			}
			
			return "redirect:/pizza";
			
		}
		
	}
	
	@GetMapping("/elimina/{pizzaId}")
	public String elimina(@Valid @PathVariable("pizzaId") Integer pizzaId, RedirectAttributes ra) {
		
		Optional<Pizza> selezionaId = repoPizza.findById(pizzaId);
		
		if(selezionaId.isPresent()) {
			
			repoPizza.deleteById(pizzaId);
			ra.addFlashAttribute("successMessage", "La pizza " + selezionaId.get().getNome() + " è stata eliminata.");
			
			return "redirect:/pizza";
			
		} else 
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pizza che stai provando ad eliminare non esiste");
		
	}
	
}