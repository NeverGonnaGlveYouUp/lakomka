import React, { createContext, useContext, useState } from 'react';

const AppContext = createContext();

export const AppProvider = ({ children }) => {
    const [counter, setCounter] = useState(0);

    const setContextCount = (value) => setCounter(value);

    return (
        <AppContext.Provider value={{ counter, setContextCount }}>
            {children}
        </AppContext.Provider>
    );
};

export const useAppContext = () => useContext(AppContext);
