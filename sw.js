const cacheName = 'veille-techno-1.0';

self.addEventListener('install', evt => {
    console.log("install du truc", evt);
});

self.addEventListener('activate', evt => {
    console.log("activation du truc", evt);
    caches.open(cacheName).then(cache => {
        cache.addAll([
            'index.html',
            'main.js',
            'add_techno.html',
            'add_techno.js',
            'contact.html'
        ])
    })
});

self.addEventListener('fetch', evt => {
    evt.respondWith(
        fetch(evt.request).then( res => {
            // we add the latest version into the cache
            caches.open(cacheName).then(cache => cache.put(evt.request, res));
            // we clone it as a response can be read only once (it's like a one time read stream)
            return res.clone();
        })
        .catch(err => caches.match(evt.request))
    );
});
