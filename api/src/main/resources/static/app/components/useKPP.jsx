import { useState, useEffect } from 'react';

export default function useKpp() {
  const [list, setList] = useState([]);
  const [selectedKpp, setSelectedKpp] = useState(() => {
    return localStorage.getItem('selectedKpp') || null;
  });

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    fetch('/api/jpersons/get-kpp-list', {
      headers: { Authorization: token ? 'Bearer ' + token : '' }
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to load KPP list');
        return res.json();
      })
      .then(data => setList(data))
      .catch(console.error);
  }, []);

  useEffect(() => {
    if (selectedKpp) localStorage.setItem('selectedKpp', selectedKpp);
    else localStorage.removeItem('selectedKpp');
  }, [selectedKpp]);

  return { list, selectedKpp, setSelectedKpp };
}
