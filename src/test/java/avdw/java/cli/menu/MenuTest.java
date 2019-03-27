package avdw.java.cli.menu;

import org.junit.Test;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MenuTest {
    @Test
    public void testMenu() {
        System.setIn(new ByteArrayInputStream("8".getBytes()));

        Menu menu = new Menu();
        menu.add(new ExampleMenu());
        menu.add(new ExampleClass());
        menu.add(new ExampleSubMenu());
        menu.add(new ExampleClass());
        menu.addSeparator();
        menu.add(new ExampleClass());
        menu.add(new ExampleClass());
        menu.display();
    }
    class ExampleMenu extends Menu {
        public ExampleMenu() {
            add(new ExampleSubMenu());
        }

        @Action
        public void exampleMenu() {
            Logger.info("exampleMenu");
        }
    }

    class ExampleSubMenu extends Menu {
        public ExampleSubMenu() {
            add(new ExampleClass());
            add(new ExampleClass());
        }

        @Action
        public void exampleSubMenu() {
            Logger.info("exampleSubMenu");
        }
    }

    class ExampleClass {
        @Action
        public void exampleClass() {
            Logger.info("exampleClass");
        }
    }

}