package com.EcoSoftware.Scrum6.Implement.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.EcoSoftware.Scrum6.Service.graficasDatos;

@Component
public class GraficasFactory {

    @Autowired
    private ApplicationContext context;

    public graficasDatos obtenerGrafica(String tipo) {

        switch (tipo.toLowerCase()) {

            case "estados":
                return context.getBean("graficaEstados", graficasDatos.class);

            case "localidades":
                return context.getBean("graficaLocalidades", graficasDatos.class);
            default:
                throw new IllegalArgumentException("Tipo de gráfica no soportado: " + tipo);
        }
    }
}
