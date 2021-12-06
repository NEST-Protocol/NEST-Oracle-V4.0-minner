package com.nest.ib.controller;

import com.nest.ib.state.Erc20State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/main")
public class MainController {
    @Autowired
    Erc20State erc20State;

    @GetMapping("")
    public ModelAndView miningData(@RequestParam(defaultValue = "base") String src) {

        ModelAndView mav = new ModelAndView("main");
        mav.addObject("src", "/" + src);
        mav.addObject("title", erc20State.token.getSymbol() + " automatic quote program");
        return mav;
    }

}
