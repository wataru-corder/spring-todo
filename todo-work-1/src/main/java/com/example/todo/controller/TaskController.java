package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.todo.service.task.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	public String list(TaskSearchForm searchForm,Model model) {
		var taskList = taskService.find(searchForm.toEntity());
		model.addAttribute("total", taskList.size());
		model.addAttribute("taskList", taskList);
		model.addAttribute("searchDTO", searchForm.toDTO());
		return "tasks/list";
	}

	@GetMapping("/{id}")
	public String showDetail(@PathVariable("id") long taskId, Model model) {

		var taskDTO = taskService.findById(taskId)
				.map(TaskDTO::toDTO)
				.orElseThrow(TaskNotFoundException::new);

		model.addAttribute("task", taskDTO);
		return "/tasks/detail";
	}

	@GetMapping("/creationForm")
	public String showCreationForm(@ModelAttribute TaskForm form,Model model) {
//		if (form == null) {
//			form = new TaskForm(null, null, null);
//		}modelatrributeで初期化もされる
		model.addAttribute("mode","CREATE");
		return "tasks/form";
	}

	@PostMapping
	public String create(@Validated TaskForm form, BindingResult bindingResult,Model model) {
		if (bindingResult.hasErrors()) {
			return showCreationForm(form,model);
		}
		taskService.create(form.toEntity());
		return "redirect:/tasks";
	}
	@GetMapping("/{id}/editForm")
	public String showEditForm(@PathVariable("id")long id, Model model) {
		var form = taskService.findById(id)
				.map(entity -> TaskForm.fromEntity(entity))
				.orElseThrow(TaskNotFoundException::new);
		model.addAttribute("taskForm",form);
		model.addAttribute("mode","EDIT");
		return "tasks/form";
	}
	@PutMapping("{id}")
	public String update(@PathVariable("id") long id, 
			@Validated @ModelAttribute TaskForm form,
			BindingResult bindingResult,
			Model model
			) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("mode" ,"EDIT");
			return "tasks/form";
		}
		var entity = form.toEntity(id);
		taskService.update(entity);
		return "redirect:/tasks/{id}";
	}
	@DeleteMapping("{id}")
	public String delete(@PathVariable("id") long id) {
		taskService.delete(id);
		return "redirect:/tasks";
	}
	
}
