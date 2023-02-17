package com.green.nowon.movie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController // @Controller + @ResponseBody
public class MovieController {

	private final MovieService service;

	// @ResponseBody 생략되어있다 무조건 적용된다.
	@GetMapping("/movies")
	public ModelAndView moviePage(ModelAndView mv) {
		mv.setViewName("movie/default");
		return mv;
	}

	@GetMapping("/movies/dailyBoxOfficeList")
	public ModelAndView dailyBoxOfficeList(ModelAndView mv, String targetDt) {
		mv.setViewName("movie/daily-boxoffice");
		mv.addObject("list", service.dailyBoxOfficeList(targetDt));
		return mv;
	}

}
