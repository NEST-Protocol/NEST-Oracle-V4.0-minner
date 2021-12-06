package com.nest.ib.controller;

import com.nest.ib.config.NestProperties;
import com.nest.ib.helper.Web3jHelper;
import com.nest.ib.model.R;

import com.nest.ib.state.MinnerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;

/**
 * Mine machine configuration
 */
@RestController
@RequestMapping("/minner")
public class MinnerController {
    private static final Logger log = LoggerFactory.getLogger(MinnerController.class);

    @Autowired
    private MinnerState minnerState;
    @Autowired
    private NestProperties nestProperties;

    @GetMapping("")
    public ModelAndView miningData() {
        ModelAndView mav = new ModelAndView("minner");
        mav.addObject("src", "/minner");
        mav.addObject("minnerState", minnerState);

        return mav;
    }

    /**
     * Enable/disable mining. True on,false off
     */
    @PostMapping("/updateMiningState")
    public R updateMiningState() {
        if (minnerState.isOpen()) {
            minnerState.closeMiner();
        } else {
            minnerState.openMiner();
        }
        return R.ok();
    }

    /**
     * Update contract parameters
     */
    @PostMapping("/updateParams")
    public R updateParams() {
        boolean ok = false;
        try {
            ok = nestProperties.updateContractParams(Web3jHelper.getWeb3j());
        } catch (Exception e) {
            log.error("Parameter update failed：{}", e);
            return R.error("Parameter update failed" + e.getMessage());
        }
        if (ok) return R.ok();
        return R.error("Parameter update failed");
    }

    /**
     * Sets the number of block intervals
     */
    @PostMapping("/updateBlockInterval")
    public R updateBlockInterval(@RequestParam(name = "blockInterval") BigInteger blockInterval) {
        minnerState.setBlockInterval(blockInterval);
        return R.ok();
    }

    @PostMapping("/updateMinnerOtherSetting")
    public R updateMinnerOtherSetting(@RequestParam(name = "closeMinNum") int closeMinNum) {
        minnerState.setCloseMinNum(closeMinNum);
        return R.ok();
    }
}
