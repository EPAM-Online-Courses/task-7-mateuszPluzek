package efs.task.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassInspector {

  /**
   * Metoda powinna wyszukać we wszystkich zadeklarowanych przez klasę polach te które oznaczone
   * są adnotacją podaną jako drugi parametr wywołania tej metody. Wynik powinien zawierać tylko
   * unikalne nazwy pól (bez powtórzeń).
   *
   * @param type       klasa (typ) poddawana analizie
   * @param annotation szukana adnotacja
   * @return lista zawierająca tylko unikalne nazwy pól oznaczonych adnotacją
   */
  public static Collection<String> getAnnotatedFields(final Class<?> type,
      final Class<? extends Annotation> annotation) {

    Set<String> fieldsWithAnnotation = new HashSet<>();

    Field[] fields = type.getDeclaredFields();
    for(Field field : fields) {
      if(field.isAnnotationPresent(annotation)) {
        fieldsWithAnnotation.add(field.getName());
      }
    }

    return fieldsWithAnnotation;
  }

  /**
   * Metoda powinna wyszukać wszystkie zadeklarowane bezpośrednio w klasie metody oraz te
   * implementowane przez nią pochodzące z interfejsów, które implementuje. Wynik powinien zawierać
   * tylko unikalne nazwy metod (bez powtórzeń).
   *
   * @param type klasa (typ) poddawany analizie
   * @return lista zawierająca tylko unikalne nazwy metod zadeklarowanych przez klasę oraz te
   * implementowane
   */
  public static Collection<String> getAllDeclaredMethods(final Class<?> type) {

    Set<String> methodsNames = new HashSet<>();
    //dodanie nazw metod bezpośrednio deklarowanych w klasie
    Method[] declaredMethods = type.getDeclaredMethods();
    for(Method method : declaredMethods) {
      methodsNames.add(method.getName());
    }
    //dodanie nazw metod deklarowanych w interfejsach implementowanych w klasie
    Class<?>[] implementedInterfaces = type.getInterfaces();
    for(Class<?> inter : implementedInterfaces) {
      Method[] interfaceMethod = inter.getDeclaredMethods();
      for(Method method : interfaceMethod) {
        methodsNames.add(method.getName());
      }
    }

    return methodsNames;
  }

  /**
   * Metoda powinna odszukać konstruktor zadeklarowany w podanej klasie który przyjmuje wszystkie
   * podane parametry wejściowe. Należy tak przygotować implementację aby nawet w przypadku gdy
   * pasujący konstruktor jest prywatny udało się poprawnie utworzyć nową instancję obiektu
   * <p>
   * Przykładowe użycia:
   * <code>ClassInspector.createInstance(Villager.class)</code>
   * <code>ClassInspector.createInstance(Villager.class, "Nazwa", "Opis")</code>
   *
   * @param type klasa (typ) którego instancje ma zostać utworzona
   * @param args parametry które mają zostać przekazane do konstruktora
   * @return nowa instancja klasy podanej jako parametr zainicjalizowana podanymi parametrami
   * @throws Exception wyjątek spowodowany nie znalezieniem odpowiedniego konstruktora
   */
  public static <T> T createInstance(final Class<T> type, final Object... args) throws Exception {
    Constructor<?>[] foundConstructors = type.getDeclaredConstructors();

    for(Constructor<?> constructor : foundConstructors) {
      //parametry konstruktora
      Class<?>[] parameters = constructor.getParameterTypes();

      if(parameters.length == args.length) {
        boolean flag = true;
        for(int i = 0;i < parameters.length; i++) {
          if(!parameters[i].isAssignableFrom(args[i].getClass())) {
            flag = false;
            break;
            //argument nie pasuje więc wychodzimy z pętli bo wiemy, że konstruktor nie pasuje
          }
        }
        if(flag) {
          constructor.setAccessible(true);  //na wypadek jeżeli konstruktor jest prywatny
          return type.cast(constructor.newInstance(args));
        }
      }

    }
    throw new Exception("No matching constructor for the given arguments\n");
  }

}
