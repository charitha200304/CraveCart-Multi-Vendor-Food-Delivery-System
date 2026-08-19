import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, ArrowRight, Utensils, Clock, Shield, Star, ChefHat, Zap } from 'lucide-react';
import { restaurantAPI } from '../utils/api';
import RestaurantCard from '../components/RestaurantCard';
import { useAuth } from '../context/AuthContext';

const CATEGORIES = ['🍕 Pizza','🍔 Burgers','🍜 Noodles','🍣 Sushi','🥗 Healthy','🌮 Mexican','🍛 Rice','🧃 Drinks'];

export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    const loadRestaurants = async () => {
      try {
        const r = await restaurantAPI.getAll();
        setRestaurants(r.data || []);
      } catch (err) {
        console.error("Failed to load restaurants", err);
      } finally {
        setLoading(false);
      }
    };

    loadRestaurants();
    const interval = setInterval(loadRestaurants, 15000); // Poll every 15s
    return () => clearInterval(interval);
  }, []);

  const filtered = restaurants.filter(r =>
    r.name?.toLowerCase().includes(search.toLowerCase()) ||
    r.address?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      {/* Hero */}
      <section style={{ background: 'linear-gradient(135deg, #0F0F1A 0%, #1A1A2E 50%, #16213E 100%)', color: 'white', padding: '80px 0 100px', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, opacity: 0.06, backgroundImage: 'radial-gradient(circle at 30% 50%, #FF4500 0%, transparent 50%), radial-gradient(circle at 70% 30%, #FFB700 0%, transparent 50%)' }} />
        <div style={{ position: 'absolute', top: '20%', right: '10%', width: '300px', height: '300px', background: 'rgba(255,69,0,0.08)', borderRadius: '50%', filter: 'blur(60px)' }} />

        <div className="container" style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ maxWidth: '680px' }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', padding: '6px 14px', background: 'rgba(255,69,0,0.15)', borderRadius: 'var(--radius-full)', border: '1px solid rgba(255,69,0,0.3)', marginBottom: '28px' }}>
              <Zap size={14} color="var(--primary)" fill="var(--primary)" />
              <span style={{ fontSize: '13px', color: '#FF6A33', fontWeight: 600 }}>Fast delivery in your area</span>
            </div>
            <h1 style={{ fontSize: 'clamp(40px, 6vw, 72px)', fontWeight: 800, lineHeight: 1.08, marginBottom: '24px', letterSpacing: '-0.03em' }}>
              Food you love,<br />
              <span style={{ color: 'var(--primary)' }}>delivered fast</span>
            </h1>
            <p style={{ fontSize: '18px', color: 'rgba(255,255,255,0.65)', marginBottom: '40px', lineHeight: 1.7, maxWidth: '500px' }}>
              Discover the best restaurants near you and get your favourite meals delivered right to your door.
            </p>

            {/* Search bar */}
            <div style={{ position: 'relative', maxWidth: '520px' }}>
              <Search size={20} style={{ position: 'absolute', left: '18px', top: '50%', transform: 'translateY(-50%)', color: 'rgba(255,255,255,0.4)', pointerEvents: 'none' }} />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search restaurants or cuisines…"
                style={{ width: '100%', padding: '18px 18px 18px 52px', borderRadius: 'var(--radius-xl)', border: '1.5px solid rgba(255,255,255,0.12)', background: 'rgba(255,255,255,0.07)', backdropFilter: 'blur(8px)', color: 'white', fontSize: '16px', outline: 'none', transition: 'all 0.2s' }}
                onFocus={e => e.target.style.borderColor = 'rgba(255,69,0,0.6)'}
                onBlur={e => e.target.style.borderColor = 'rgba(255,255,255,0.12)'}
              />
              <Link to="/restaurants" style={{ position: 'absolute', right: '8px', top: '50%', transform: 'translateY(-50%)' }}>
                <button className="btn btn-primary" style={{ borderRadius: 'var(--radius-lg)', padding: '10px 20px' }}>
                  Search <ArrowRight size={16} />
                </button>
              </Link>
            </div>

            {/* Stats */}
            <div style={{ display: 'flex', gap: '32px', marginTop: '48px', flexWrap: 'wrap' }}>
              {[['500+','Restaurants'],['50K+','Happy Customers'],['4.8★','Average Rating']].map(([val, label]) => (
                <div key={label}>
                  <div style={{ fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: '28px', color: 'white' }}>{val}</div>
                  <div style={{ fontSize: '13px', color: 'rgba(255,255,255,0.5)', marginTop: '2px' }}>{label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Categories */}
      <section style={{ background: 'white', padding: '32px 0', borderBottom: '1px solid var(--border)' }}>
        <div className="container">
          <div style={{ display: 'flex', gap: '10px', overflowX: 'auto', paddingBottom: '4px', scrollbarWidth: 'none' }}>
            {CATEGORIES.map(cat => (
              <button key={cat} className="chip" style={{ flexShrink: 0 }}>{cat}</button>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: '64px 0', background: 'var(--bg)' }}>
        <div className="container">
          <div className="grid-3" style={{ gap: '24px' }}>
            {[
              { icon: Utensils, color: '#FF4500', bg: '#FFF5F2', title: 'Wide Selection', desc: 'Hundreds of restaurants across all cuisines to choose from' },
              { icon: Clock, color: '#3B82F6', bg: '#EFF6FF', title: 'Fast Delivery', desc: 'Track your order in real-time with estimated delivery times' },
              { icon: Shield, color: '#10B981', bg: '#ECFDF5', title: 'Safe & Secure', desc: 'Secure payments and guaranteed fresh food, every time' },
            ].map(({ icon: Icon, color, bg, title, desc }) => (
              <div key={title} style={{ background: 'white', borderRadius: 'var(--radius-lg)', padding: '28px', border: '1px solid var(--border)', textAlign: 'center' }}>
                <div style={{ width: '56px', height: '56px', background: bg, borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
                  <Icon size={26} color={color} />
                </div>
                <h3 style={{ fontSize: '18px', marginBottom: '8px' }}>{title}</h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '14px', lineHeight: 1.6 }}>{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Restaurants */}
      <section style={{ padding: '0 0 80px' }}>
        <div className="container">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '32px' }}>
            <div>
              <h2 className="section-title" style={{ marginBottom: '4px' }}>Popular Restaurants</h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>{restaurants.length} restaurants available</p>
            </div>
            <Link to="/restaurants" className="btn btn-ghost">View all <ArrowRight size={16} /></Link>
          </div>

          {loading ? (
            <div className="grid-auto">
              {[1,2,3,4].map(i => (
                <div key={i} className="card">
                  <div className="skeleton" style={{ height: '180px' }} />
                  <div className="card-body" style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <div className="skeleton" style={{ height: '20px', width: '70%' }} />
                    <div className="skeleton" style={{ height: '14px', width: '100%' }} />
                    <div className="skeleton" style={{ height: '14px', width: '60%' }} />
                  </div>
                </div>
              ))
            </div>
          ) : filtered.length === 0 ? (
            <div className="empty-state">
              <ChefHat size={64} className="empty-state-icon" />
              <h3>No restaurants found</h3>
              <p>Try a different search or check back later</p>
            </div>
          ) : (
            <div className="grid-auto">
              {filtered.slice(0, 8).map(r => <RestaurantCard key={r.id} restaurant={r} />)}
            </div>
          )}
        </div>
      </section>

      {/* CTA */}
      {!isAuthenticated && (
        <section style={{ background: 'var(--primary)', padding: '80px 0' }}>
          <div className="container" style={{ textAlign: 'center' }}>
            <h2 style={{ color: 'white', fontSize: 'clamp(28px, 4vw, 44px)', marginBottom: '16px' }}>Ready to order?</h2>
            <p style={{ color: 'rgba(255,255,255,0.8)', fontSize: '18px', marginBottom: '32px' }}>Join thousands of happy customers today</p>
            <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', flexWrap: 'wrap' }}>
              <Link to="/register" className="btn btn-lg" style={{ background: 'white', color: 'var(--primary)' }}>Create Account</Link>
              <Link to="/login" className="btn btn-lg" style={{ background: 'rgba(255,255,255,0.15)', color: 'white', border: '1.5px solid rgba(255,255,255,0.4)' }}>Sign In</Link>
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
