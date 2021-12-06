package com.nest.ib.controller;

import com.nest.ib.model.R;
import com.nest.ib.state.GasPriceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;


@RestController
@RequestMapping("/gasPrice")
public class GasPriceController {


    @Autowired
    private GasPriceState gasPriceState;

    @GetMapping("")
    public ModelAndView miningData() {
        ModelAndView mav = new ModelAndView("gasPrice");
        // gasPrice
        mav.addObject("gasPriceState", gasPriceState);
        return mav;
    }


    /**
     * Update quote GASPRICE double value
     *
     * @param baseOfferGasPriceMul   Base quotation multiple
     * @param closeSheetGasPriceMul  Defrost multiple
     * @param cancelOfferGasPriceMul Cancel trade multiple
     * @return
     */
    @PostMapping("/updateGasPrice")
    public R updateGasPrice(@RequestParam(name = "baseOfferGasPriceMul") BigDecimal baseOfferGasPriceMul,
                            @RequestParam(name = "closeSheetGasPriceMul") BigDecimal closeSheetGasPriceMul,
                            @RequestParam(name = "cancelOfferGasPriceMul") BigDecimal cancelOfferGasPriceMul,
                            @RequestParam(name = "withdrawGasPriceMul") BigDecimal withdrawGasPriceMul) {

        gasPriceState.getBaseOfferType().setGasPriceMul(baseOfferGasPriceMul);
        //
        gasPriceState.getCloseSheet().setGasPriceMul(closeSheetGasPriceMul);
        //
        gasPriceState.getWithdrawType().setGasPriceMul(withdrawGasPriceMul);
        //
        gasPriceState.setCancelOfferGasPriceMul(cancelOfferGasPriceMul);

        return R.ok();
    }
}
