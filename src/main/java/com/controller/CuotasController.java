/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.entitys.Detalles;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.util.httpCuotas;

/**
 *
 * @author intercitydev
 */
@Controller
public class CuotasController {

    HttpSession sesion;
    String sesionUser;
    String mensaje;

    @RequestMapping("cuotas.htm")
    public ModelAndView getCuotas(HttpServletRequest request) {
        sesion = request.getSession();
        ModelAndView mav = new ModelAndView();
        String mensaje = null;
        Detalles detalle = (Detalles) sesion.getAttribute("cuenta");

        String country = detalle.getCiudad();
        String amount = "5";
        httpCuotas cuotasHelper = new httpCuotas();
        String resultado = cuotasHelper.getCuotas(country, amount);

        if (sesion.getAttribute("usuario") == null) {

            mav.setViewName("login/login");

        } else {
            sesionUser = sesion.getAttribute("usuario").toString();
            //Detalles detalle = (Detalles) sesion.getAttribute("cuenta");
            mav.addObject("country", detalle.getCiudad());
            mav.addObject("resultado", resultado);
            mensaje = "Lista de Cuotas";
            mav.addObject("mensaje", mensaje);
            mav.addObject("amount", amount);
            mav.addObject("country", country);

            if (sesion.getAttribute("tipoUsuario").toString().compareTo("Administrador") == 0) {
                mav.setViewName("viewsAdmin/cuotasAdmin");
                System.out.println("el usuario es administrador");
            } else {
                mav.setViewName("panel/cuotas");
            }
        }
        return mav;
    }

    @RequestMapping(value = "postCuotas.htm", method = RequestMethod.POST)
    public ModelAndView postCuotas(HttpServletRequest request) {

        sesion = request.getSession();
        String country = request.getParameter("country");
        String amount = request.getParameter("amount");

        Detalles detalle = (Detalles) sesion.getAttribute("cuenta");
        System.out.print(detalle.getCiudad());

        ModelAndView mav = new ModelAndView();
        if (sesion.getAttribute("usuario") == null) {

            mav.setViewName("login/login");

        } else {
            httpCuotas cuotasHelper = new httpCuotas();
            String resultado =  cuotasHelper.getCuotas(country, amount);
            sesionUser = sesion.getAttribute("usuario").toString();
            //Detalles detalle = (Detalles) sesion.getAttribute("cuenta");
            mav.addObject("country", detalle.getCiudad());
            mav.addObject("resultado", resultado);
            mensaje = "Lista de Cuotas";
            mav.addObject("mensaje", mensaje);
            mav.addObject("amount", amount);
            mav.addObject("country", country);

            if (sesion.getAttribute("tipoUsuario").toString().compareTo("Administrador") == 0) {
                mav.setViewName("viewsAdmin/cuotasAdmin");
                System.out.println("el usuario es administrador");
            } else {
                mav.setViewName("panel/cuotas");
            }
           
        }
        return mav;

    }
}
