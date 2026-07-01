function toggleJobSelection() {
    const container = document.getElementById('job-selection-container');
    container.classList.toggle('hidden', !document.getElementById('assign-job-check').checked);
    updateJobDropdown();
}

function updateJobDropdown() {
    const select = document.getElementById('calc-job-id');
    const jobs = JSON.parse(localStorage.getItem('jobs') || '[]');
    select.innerHTML = '<option value="">-- Create New Job --</option>';
    jobs.forEach(job => {
        const opt = document.createElement('option');
        opt.value = job.id;
        opt.textContent = job.name;
        select.appendChild(opt);
    });
}

function loadJobs() {
    const container = document.getElementById('jobs-container');
    if (!container) return;
    container.innerHTML = '';
    const jobs = JSON.parse(localStorage.getItem('jobs') || '[]');
    const history = JSON.parse(localStorage.getItem('calcHistory') || '[]');
    const symbol = getCurrency();

    const unassigned = history.filter(h => !h.jobId);
    if (unassigned.length > 0) renderJobFolder(container, { id: 'unassigned', name: 'Unassigned Calculations' }, unassigned, symbol, true);

    jobs.forEach(job => {
        const jobCalcs = history.filter(h => h.jobId == job.id);
        renderJobFolder(container, job, jobCalcs, symbol, false);
    });
}

function clearCacheStep1() {
    if (confirm("⚠️ DANGER: This will permanently delete EVERYTHING (Inventory, Printers, History). Continue?")) {
        if (confirm("FINAL WARNING: This cannot be undone. Wipe all data?")) {
            localStorage.clear();
            location.reload();
        }
    }
}