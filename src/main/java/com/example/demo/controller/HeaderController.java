package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/header")
public class HeaderController {

	@RequestMapping(value = "/goMenu", params = "back", method = RequestMethod.POST)
	public String back(Model model, HttpSession session,RedirectAttributes redirectAttributes) {
		return "redirect:/menu/loaded";
	}

	//更新ボタンが押された場合の処理
//	@RequestMapping(value = "/goMenu",method = RequestMethod.GET)
//	public String reload(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
//		return "redirect:/menu";
//	}
}
