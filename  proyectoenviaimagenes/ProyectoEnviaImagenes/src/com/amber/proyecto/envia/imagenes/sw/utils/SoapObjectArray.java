package com.amber.proyecto.envia.imagenes.sw.utils;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class SoapObjectArray extends SoapObject
{

    /**
     * Crea una instancia de {@link ExtendedSoapObject}
     * 
     * @param namespace
     *            namespace del objeto
     * @param name
     *            nombre del objeto
     */
    public SoapObjectArray(String namespace, String name)
    {
        super(namespace, name);
    }

    /**
     * Crea una instancia de {@link ExtendedSoapObject} a partir de una
     * instancia de la clase base.
     * 
     * @param o
     *            instancia de {@link SoapObject}
     */
    public SoapObjectArray(SoapObject o)
    {
        super(o.getNamespace(), o.getName());
        for (int i = 0; i < o.getAttributeCount(); i++)
        {
            AttributeInfo ai = new AttributeInfo();
            o.getAttributeInfo(i, ai);
            ai.setValue(o.getAttribute(i));
            addAttribute(ai);
        }

        for (int i = 0; i < o.getPropertyCount(); i++)
        {
            PropertyInfo pi = new PropertyInfo();
            o.getPropertyInfo(i, pi);
            pi.setValue(o.getProperty(i));
            addProperty(pi);
        }
    }

    /**
     * Permite pasar objetos de tipo array.
     */
    @Override
    public SoapObject addProperty(String name, Object value)
    {
        if (value instanceof Object[])
        {
            Object[] subValues = (Object[]) value;
            for (int i = 0; i < subValues.length; i++)
            {
                super.addProperty(name, subValues[i]);
            }
        }
        else
        {
            super.addProperty(name, value);
        }

        return this;
    }

    /**
     * Este método devuelve un objeto {@link SoapObject} o valor primitivo
     * {@link SoapPrimitive} o un array de los mismos. Puede devolver null si es
     * el valor que tiene la propiedad (porque fue lo que devolvió el método
     * remoto) o si no se encuentra la propiedad.
     */
    @Override
    public Object getProperty(String name)
    {
        List<Object> result = new ArrayList<Object>();

        for (int i = 0; i < properties.size(); i++)
        {
            PropertyInfo prop = (PropertyInfo) properties.elementAt(i);
            if (prop.getName() != null && prop.getName().equals(name))
            {
                result.add(unwrap(prop));
            }
        }

        if (result.size() == 1)
        {
            return result.get(0);
        }
        else if (result.size() > 1)
        {
            return result.toArray(new Object[0]);
        }
        else
        {
            return null;
        }
    }

    /**
     * Este método siempre devuelve un array de objetos.
     * 
     * @param name
     * @return
     */
    public Object[] getArrayProperty(String name)
    {
        Object o = getProperty(name);
        Object values[] = null;
        if (o != null)
        {
            if (o instanceof Object[])
            {
                values = (Object[]) o;
            }
            else
            {
                values = new Object[1];
                values[0] = o;
            }
        }

        return values;
    }

    Object unwrap(Object o)
    {
        if (o instanceof PropertyInfo)
        {
            return unwrap(((PropertyInfo) o).getValue());
        }
        else if (o instanceof SoapPrimitive || o instanceof SoapObject)
        {
            return o;
        }

        return null;
    }
}