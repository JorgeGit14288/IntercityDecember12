/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.dao.TelefonosDao;
import com.dao.UsuariosDao;
import com.entitys.Detalles;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.entitys.Telefonos;
import com.entitys.Usuarios;
import com.jsonEntitys.Account;
import com.util.Cifrar;
import com.util.httpAccount;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author jorge
 */
@Controller
@SessionAttributes({"userSession"})
public class LoginController {

    HttpSession sesion;
    String sesionUser;

    // este metodo devolvera la vista del login vacia 
    @RequestMapping("login.htm")
    public ModelAndView Login() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login/login");
        return mav;

    }

// este metodo sirve para validar el login
    @RequestMapping(value = "validarLogin.htm", method = RequestMethod.POST)
    public ModelAndView Validarlogin(HttpServletRequest request) {
        // creamos los objetos a utilizar
        ModelAndView mav = new ModelAndView();
        Telefonos tel2 = new Telefonos();
        TelefonosDao dao = new TelefonosDao();
        Usuarios user = new Usuarios();
        UsuariosDao userDao = new UsuariosDao();
        String mensaje = null;
        try {
            // recogemos los parametros
            sesion = request.getSession();
            String telefonoArea = (request.getParameter("codigo") + "-" + request.getParameter("telefono"));
            System.out.println("El telefono a busar es " + telefonoArea);
            Cifrar varCifrar = new Cifrar();
            String pass = varCifrar.Encriptar(request.getParameter("password"));

            tel2 = dao.getTelefono(telefonoArea);

            if (tel2 == null) {
                mensaje = "El usuario no existe en la base de datos";
                System.out.println("ha ocurrido un error");
                mav.setViewName("login/login");

            } else {
                user = userDao.getUsuario(tel2.getUsuarios().getIdUsuario());
                System.out.println("Los nombres del usuario " + user.getNombres());
                System.out.println("DATOS EN SERVIDOR" + tel2.getTelefonoArea() + user.getPassword());
                System.out.println("DATOS DEL USUARIO " + telefonoArea + pass);

                if ((telefonoArea.compareTo(tel2.getTelefonoArea()) == 0) && (pass.compareTo(user.getPassword()) == 0)) {
                    String tipoUsuario = user.getTipoUsuario();
                    String userSesion = tel2.getTelefonoArea();
                    // String temp = userSesion.replace("-", "");
                    //  System.out.println(temp);

                    Account account = new Account();
                    httpAccount accountHelper = new httpAccount();
                    Detalles cuenta = new Detalles();

                    cuenta.setIdUsuaro(user.getIdUsuario());
                    cuenta.setTelefono(telefonoArea);
                    cuenta.setAccountId(user.getIdAccount());
                    cuenta.setNombres(user.getNombres());
                    cuenta.setApellidos(user.getApellidos());
                    cuenta.setCiudad(user.getPais());
                    cuenta.setEmail(user.getEmail());

                    try {
                        account = accountHelper.getAccountObject(userSesion);
                        if (account.getId() != null) {
                            cuenta.setDireccion(account.getAddress1());
                            cuenta.setCodigoPostal(account.getPostal_code());
                            cuenta.setLenguaje(account.getLanguaje_id());
                            cuenta.setNotifiEmail(account.getNotify_email());
                            cuenta.setNotifiFlag(account.getNotify_flag());
                            cuenta.setSaldo(account.getBalance());
                            System.out.print("El balance de la cuenta es " + cuenta.getSaldo());

                        } else {
                            System.out.print("no pudo llenarse por completo el objeto");
                           

                        }

                    } catch (Exception e) {
                        System.out.println("\n EL SERVIDOR http://192.168.5.44 NO RESPONDIO \n");
                    }

                    sesion.setAttribute("usuario", userSesion);
                    sesion.setAttribute("tipoUsuario", tipoUsuario);
                    sesion.setAttribute("cuenta", cuenta);
                    if (tipoUsuario.compareTo("Administrador") == 0) {
                        System.out.println("REDIRIGIENDO A VISTAS ADMINISTRADOR");
                        mensaje = "Bienvenido";
                        mav.setViewName("viewsAdmin/panelAdmin");
                    } else {
                        System.out.println("REDIRIGIENDO A VISTAS USUARIOS");
                        mensaje = "Bienvenido";
                        mav.setViewName("panel/panel");
                    }

                } else {
                    mensaje = "LOS DATOS NO SON CORRECTOS";
                    System.out.println("ha ocurrido un error");
                    mav.setViewName("login/login");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("ha ocurrido un error");
            mensaje = "Lo sentimos, ha ocurrido un error";
            mav.setViewName("login/login");
        }
        mav.addObject("mensaje", mensaje);

        return mav;
    }

    // este metodo devolvera la vista para registrar a un usuario. tambien vacia
    @RequestMapping(value = "logout.htm", method = RequestMethod.POST)
    public ModelAndView LogOutPOST(HttpServletRequest request
    ) {

        sesion = null;
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login/login");
        return mav;

    }

    @RequestMapping(value = "logout.htm", method = RequestMethod.GET)
    public ModelAndView LogOutGET(HttpServletRequest request
    ) {
        sesion = request.getSession();
        sesion.invalidate();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login/login");
        return mav;

    }

}
