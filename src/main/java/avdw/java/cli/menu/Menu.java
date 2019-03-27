package avdw.java.cli.menu;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    Scanner scanner = new Scanner(System.in);
    Map<Integer, Item> itemMap = new HashMap();

    public Menu() {
        addActionsFrom(this);
    }

    public void display() {
        Boolean isLooping = Boolean.TRUE;
        while (isLooping) {
            System.out.println(String.format("%n <= %s =>", this.getClass().getSimpleName()));
            itemMap.entrySet().stream().forEachOrdered(entry -> {
                System.out.println(String.format("%2s: %s", entry.getKey(), entry.getValue().title));
            });
            System.out.println(String.format("%2s: <= Back", itemMap.size() + 1));
            System.out.print("\nChoice > ");

            Integer option = scanner.nextInt();
            if (itemMap.containsKey(option)) {
                Item item = itemMap.get(option);

                if (item.action.isPresent()) {
                    try {
                        List<Object> args = new ArrayList();
                        Method method = item.action.get();
                        Arrays.stream(method.getParameters()).forEach(parameter -> {
                            if (String.class.equals(parameter.getType())) {
                                System.out.print(String.format("%s > ", parameter.getType().getSimpleName()));
                                args.add(scanner.next());
                            }
                        });
                        item.action.get().invoke(item.context.get(), args.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Logger.info(e);
                    }
                } else if (item != Item.SEPARATOR) {
                    Logger.warn(String.format("option %s has no action mapped", option));
                }
            } else {
                if (option == itemMap.size() + 1) {
                    isLooping = Boolean.FALSE;
                } else {
                    Logger.warn(String.format("no option %s exists", option));
                }
            }
        }
    }

    public void add(Object object) {
        if (Menu.class.isInstance(object)) {
            try {
                itemMap.put(itemMap.size() + 1, new Item(String.format("%s =>", object.getClass().getSimpleName()),
                        object,
                        object.getClass().getMethod("display")));
            } catch (NoSuchMethodException e) {
                Logger.error(e);
            }
        } else {
            addActionsFrom(object);
        }
    }

    public void addSeparator() {
        itemMap.put(itemMap.size() + 1, Item.SEPARATOR);
    }

    private void addActionsFrom(Object object) {
        ReflectionUtils.getAllMethods(object.getClass(), ReflectionUtils.withAnnotation(Action.class)).stream()
                .forEach(action -> {
                    String parameters = Arrays.stream(action.getParameters())
                            .map(parameter -> parameter.getType().getSimpleName())
                            .collect(Collectors.joining(", "));
                    itemMap.put(itemMap.size() + 1, new Item(String.format("%s.%s(%s)", object.getClass().getSimpleName(), action.getName(), parameters), object, action));
                });
    }

    static class Item {

        public static Item SEPARATOR = new Item(StringUtils.repeat("=", 32));

        public final String title;
        public final Optional<Method> action;
        public final Optional<Object> context;

        public Item(String title, Object context, Method action) {
            this.title = title;
            this.context = Optional.of(context);
            this.action = Optional.of(action);
        }

        public Item(String title) {
            this.title = title;
            this.context = Optional.empty();
            this.action = Optional.empty();
        }
    }
}
