package io.zero88.qwe.component;

import java.util.Collection;

interface ContextLookupInternal extends ContextLookup {

    Collection<ComponentContext> list();

    void add(ComponentContext context);

}
