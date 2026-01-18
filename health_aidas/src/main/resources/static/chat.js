// --- 1. TOKEN MANAGEMENT ---
const urlParams = new URLSearchParams(window.location.search);
const tokenFromUrl = urlParams.get('token');

if (tokenFromUrl) {
    sessionStorage.setItem('jwtToken', tokenFromUrl);
    console.log("✅ Token saved from URL");
    window.history.replaceState({}, document.title, "/dashboard");
}

// --- 2. API BASE URL (CHANGES BASED ON ENVIRONMENT) ---
const API_BASE_URL = window.location.hostname === 'localhost'
    ? 'http://localhost:7070'  // Local development
    : window.location.origin;   // Production (Render)

console.log("🌐 API Base URL:", API_BASE_URL);

// --- 3. CHATBOT LOGIC ---
const chatBox = document.querySelector('.chat-box');
const input = document.querySelector('.input-area input');
const button = document.querySelector('.input-area button');

// Add welcome message
chatBox.innerHTML = `<div style="color: #4facfe; margin-bottom: 10px;"><b>Deep AI:</b> Hello! I'm Deep, your health assistant. How can I help you today?</div>`;

button.addEventListener('click', sendMessage);
input.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') sendMessage();
});

async function sendMessage() {
    const userMessage = input.value.trim();
    if (!userMessage) return;

    // Display user message
    addMessage('You', userMessage, '#333');
    input.value = '';

    // Get token
    const savedToken = sessionStorage.getItem('jwtToken');

    if (!savedToken) {
        addMessage('System', 'No token found. Redirecting to login...', 'red');
        setTimeout(() => {
            window.location.href = API_BASE_URL.replace('7070', '7071') + '/login';
        }, 2000);
        return;
    }

    // Show loading
    const loadingId = 'loading-' + Date.now();
    chatBox.innerHTML += `<div id="${loadingId}" style="color: #4facfe;"><b>Deep AI:</b> Thinking... 🤔</div>`;
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        const response = await fetch(`${API_BASE_URL}/api/ai/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + savedToken
            },
            body: JSON.stringify({
                message: userMessage,
                username: 'User',
                isFirst: false
            })
        });

        // Remove loading message
        document.getElementById(loadingId).remove();

        if (response.status === 403 || response.status === 401) {
            addMessage('System', 'Session expired. Please login again.', 'red');
            setTimeout(() => {
                window.location.href = API_BASE_URL.replace('7070', '7071') + '/login';
            }, 2000);
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.text();
        addMessage('Deep AI', data, '#4facfe');

    } catch (err) {
        document.getElementById(loadingId)?.remove();
        console.error("❌ Error:", err);
        addMessage('System', 'Error connecting to server. Please try again.', 'red');
    }
}

function addMessage(sender, text, color) {
    const msgDiv = document.createElement('div');
    msgDiv.style.marginBottom = '10px';
    msgDiv.style.color = color;
    msgDiv.innerHTML = `<b>${sender}:</b> ${text}`;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}