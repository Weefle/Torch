package net.minecraft.server;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.event.entity.EntityTeleportEvent;
// CraftBukkit end

public class EntityShulker extends EntityGolem implements IMonster {

    private static final UUID by = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier bz = (new AttributeModifier(EntityShulker.by, "Covered armor bonus", 20.0D, 0)).a(false);
    protected static final DataWatcherObject<EnumDirection> a = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.l);
    protected static final DataWatcherObject<Optional<BlockPosition>> b = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.k);
    protected static final DataWatcherObject<Byte> c = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Byte> bw = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.a); // PAIL: protected -> public, rename COLOR
    public static final EnumColor bx = EnumColor.PURPLE;
    private float bA;
    private float bB;
    private BlockPosition bC;
    private int bD;

    public EntityShulker(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.aO = 180.0F;
        this.aN = 180.0F;
        this.fireProof = true;
        this.bC = null;
        this.b_ = 5;
    }

    @Override
	@Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        this.aN = 180.0F;
        this.aO = 180.0F;
        this.yaw = 180.0F;
        this.lastYaw = 180.0F;
        this.aP = 180.0F;
        this.aQ = 180.0F;
        return super.prepare(difficultydamagescaler, groupdataentity);
    }

    @Override
	protected void r() {
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(4, new EntityShulker.a());
        this.goalSelector.a(7, new EntityShulker.e(null));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(2, new EntityShulker.d(this));
        this.targetSelector.a(3, new EntityShulker.c(this));
    }

    @Override
	protected boolean playStepSound() {
        return false;
    }

    @Override
	public SoundCategory bC() {
        return SoundCategory.HOSTILE;
    }

    @Override
	protected SoundEffect G() {
        return SoundEffects.fz;
    }

    @Override
	public void D() {
        if (!this.do_()) {
            super.D();
        }

    }

    @Override
	protected SoundEffect bX() {
        return SoundEffects.fF;
    }

    @Override
	protected SoundEffect bW() {
        return this.do_() ? SoundEffects.fH : SoundEffects.fG;
    }

    @Override
	protected void i() {
        super.i();
        this.datawatcher.register(EntityShulker.a, EnumDirection.DOWN);
        this.datawatcher.register(EntityShulker.b, Optional.absent());
        this.datawatcher.register(EntityShulker.c, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityShulker.bw, Byte.valueOf((byte) EntityShulker.bx.getColorIndex()));
    }

    @Override
	protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
    }

    @Override
	protected EntityAIBodyControl s() {
        return new EntityShulker.b(this);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityShulker.class);
    }

    @Override
	public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.datawatcher.set(EntityShulker.a, EnumDirection.fromType1(nbttagcompound.getByte("AttachFace")));
        this.datawatcher.set(EntityShulker.c, Byte.valueOf(nbttagcompound.getByte("Peek")));
        this.datawatcher.set(EntityShulker.bw, Byte.valueOf(nbttagcompound.getByte("Color")));
        if (nbttagcompound.hasKey("APX")) {
            int i = nbttagcompound.getInt("APX");
            int j = nbttagcompound.getInt("APY");
            int k = nbttagcompound.getInt("APZ");

            this.datawatcher.set(EntityShulker.b, Optional.of(new BlockPosition(i, j, k)));
        } else {
            this.datawatcher.set(EntityShulker.b, Optional.<BlockPosition>absent());
        }

    }

    @Override
	public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("AttachFace", (byte) this.datawatcher.get(EntityShulker.a).a());
        nbttagcompound.setByte("Peek", this.datawatcher.get(EntityShulker.c).byteValue());
        nbttagcompound.setByte("Color", this.datawatcher.get(EntityShulker.bw).byteValue());
        BlockPosition blockposition = this.di();

        if (blockposition != null) {
            nbttagcompound.setInt("APX", blockposition.getX());
            nbttagcompound.setInt("APY", blockposition.getY());
            nbttagcompound.setInt("APZ", blockposition.getZ());
        }

    }

    @Override
	public void A_() {
        super.A_();
        BlockPosition blockposition = (BlockPosition) ((Optional) this.datawatcher.get(EntityShulker.b)).orNull();

        if (blockposition == null && !this.world.isClientSide) {
            blockposition = new BlockPosition(this);
            this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
        }

        float f;

        if (this.isPassenger()) {
            blockposition = null;
            f = this.bB().yaw;
            this.yaw = f;
            this.aN = f;
            this.aO = f;
            this.bD = 0;
        } else if (!this.world.isClientSide) {
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getMaterial() != Material.AIR) {
                EnumDirection enumdirection;

                if (iblockdata.getBlock() == Blocks.PISTON_EXTENSION) {
                    enumdirection = iblockdata.get(BlockPiston.FACING);
                    if (this.world.isEmpty(blockposition.shift(enumdirection))) {
                        blockposition = blockposition.shift(enumdirection);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
                    } else {
                        this.o();
                    }
                } else if (iblockdata.getBlock() == Blocks.PISTON_HEAD) {
                    enumdirection = iblockdata.get(BlockPistonExtension.FACING);
                    if (this.world.isEmpty(blockposition.shift(enumdirection))) {
                        blockposition = blockposition.shift(enumdirection);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
                    } else {
                        this.o();
                    }
                } else {
                    this.o();
                }
            }

            BlockPosition blockposition1 = blockposition.shift(this.dh());

            if (!this.world.d(blockposition1, false)) {
                boolean flag = false;
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection1 = aenumdirection[j];

                    blockposition1 = blockposition.shift(enumdirection1);
                    if (this.world.d(blockposition1, false)) {
                        this.datawatcher.set(EntityShulker.a, enumdirection1);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    this.o();
                }
            }

            BlockPosition blockposition2 = blockposition.shift(this.dh().opposite());

            if (this.world.d(blockposition2, false)) {
                this.o();
            }
        }

        f = this.dj() * 0.01F;
        this.bA = this.bB;
        if (this.bB > f) {
            this.bB = MathHelper.a(this.bB - 0.05F, f, 1.0F);
        } else if (this.bB < f) {
            this.bB = MathHelper.a(this.bB + 0.05F, 0.0F, f);
        }

        if (blockposition != null) {
            if (this.world.isClientSide) {
                if (this.bD > 0 && this.bC != null) {
                    --this.bD;
                } else {
                    this.bC = blockposition;
                }
            }

            this.locX = blockposition.getX() + 0.5D;
            this.locY = blockposition.getY();
            this.locZ = blockposition.getZ() + 0.5D;
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.M = this.locX;
            this.N = this.locY;
            this.O = this.locZ;
            double d0 = 0.5D - MathHelper.sin((0.5F + this.bB) * 3.1415927F) * 0.5D;
            double d1 = 0.5D - MathHelper.sin((0.5F + this.bA) * 3.1415927F) * 0.5D;
            double d2 = d0 - d1;
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            EnumDirection enumdirection2 = this.dh();

            switch (enumdirection2) {
            case DOWN:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D + d0, this.locZ + 0.5D));
                d4 = d2;
                break;

            case UP:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY - d0, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d4 = -d2;
                break;

            case NORTH:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D + d0));
                d5 = d2;
                break;

            case SOUTH:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D - d0, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d5 = -d2;
                break;

            case WEST:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D + d0, this.locY + 1.0D, this.locZ + 0.5D));
                d3 = d2;
                break;

            case EAST:
                this.a(new AxisAlignedBB(this.locX - 0.5D - d0, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d3 = -d2;
            }

            if (d2 > 0.0D) {
                List list = this.world.getEntities(this, this.getBoundingBox());

                if (!list.isEmpty()) {
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        if (!(entity instanceof EntityShulker) && !entity.noclip) {
                            entity.move(EnumMoveType.SHULKER, d3, d4, d5);
                        }
                    }
                }
            }
        }

    }

    @Override
	public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        if (enummovetype == EnumMoveType.SHULKER_BOX) {
            this.o();
        } else {
            super.move(enummovetype, d0, d1, d2);
        }

    }

    @Override
	public void setPosition(double d0, double d1, double d2) {
        super.setPosition(d0, d1, d2);
        if (this.datawatcher != null && this.ticksLived != 0) {
            Optional optional = this.datawatcher.get(EntityShulker.b);
            Optional optional1 = Optional.of(new BlockPosition(d0, d1, d2));

            if (!optional1.equals(optional)) {
                this.datawatcher.set(EntityShulker.b, optional1);
                this.datawatcher.set(EntityShulker.c, Byte.valueOf((byte) 0));
                this.impulse = true;
            }

        }
    }

    protected boolean o() {
        if (!this.hasAI() && this.isAlive()) {
            BlockPosition blockposition = new BlockPosition(this);

            for (int i = 0; i < 5; ++i) {
                BlockPosition blockposition1 = blockposition.a(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));

                if (blockposition1.getY() > 0 && this.world.isEmpty(blockposition1) && this.world.g(this) && this.world.getCubes(this, new AxisAlignedBB(blockposition1)).isEmpty()) {
                    boolean flag = false;
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];

                        if (this.world.d(blockposition1.shift(enumdirection), false)) {
                            // CraftBukkit start
                            EntityTeleportEvent teleport = new EntityTeleportEvent(this.getBukkitEntity(), this.getBukkitEntity().getLocation(), new Location(this.world.getWorld(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()));
                            this.world.getServer().getPluginManager().callEvent(teleport);
                            if (!teleport.isCancelled()) {
                                Location to = teleport.getTo();
                                blockposition1 = new BlockPosition(to.getX(), to.getY(), to.getZ());

                                this.datawatcher.set(EntityShulker.a, enumdirection);
                                flag = true;
                            }
                            // CraftBukkit end
                            break;
                        }
                    }

                    if (flag) {
                        this.a(SoundEffects.fK, 1.0F, 1.0F);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition1));
                        this.datawatcher.set(EntityShulker.c, Byte.valueOf((byte) 0));
                        this.setGoalTarget((EntityLiving) null);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
	public void n() {
        super.n();
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.aO = 180.0F;
        this.aN = 180.0F;
        this.yaw = 180.0F;
    }

    @Override
	public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityShulker.b.equals(datawatcherobject) && this.world.isClientSide && !this.isPassenger()) {
            BlockPosition blockposition = this.di();

            if (blockposition != null) {
                if (this.bC == null) {
                    this.bC = blockposition;
                } else {
                    this.bD = 6;
                }

                this.locX = blockposition.getX() + 0.5D;
                this.locY = blockposition.getY();
                this.locZ = blockposition.getZ() + 0.5D;
                this.lastX = this.locX;
                this.lastY = this.locY;
                this.lastZ = this.locZ;
                this.M = this.locX;
                this.N = this.locY;
                this.O = this.locZ;
            }
        }

        super.a(datawatcherobject);
    }

    @Override
	public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.do_()) {
            Entity entity = damagesource.i();

            if (entity instanceof EntityArrow) {
                return false;
            }
        }

        if (super.damageEntity(damagesource, f)) {
            if (this.getHealth() < this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
                this.o();
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean do_() {
        return this.dj() == 0;
    }

    @Override
	@Nullable
    public AxisAlignedBB ag() {
        return this.isAlive() ? this.getBoundingBox() : null;
    }

    public EnumDirection dh() {
        return this.datawatcher.get(EntityShulker.a);
    }

    @Nullable
    public BlockPosition di() {
        return (BlockPosition) ((Optional) this.datawatcher.get(EntityShulker.b)).orNull();
    }

    public void g(@Nullable BlockPosition blockposition) {
        this.datawatcher.set(EntityShulker.b, Optional.fromNullable(blockposition));
    }

    public int dj() {
        return this.datawatcher.get(EntityShulker.c).byteValue();
    }

    public void a(int i) {
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.g).c(EntityShulker.bz);
            if (i == 0) {
                this.getAttributeInstance(GenericAttributes.g).b(EntityShulker.bz);
                this.a(SoundEffects.fE, 1.0F, 1.0F);
            } else {
                this.a(SoundEffects.fI, 1.0F, 1.0F);
            }
        }

        this.datawatcher.set(EntityShulker.c, Byte.valueOf((byte) i));
    }

    @Override
	public float getHeadHeight() {
        return 0.5F;
    }

    @Override
	public int N() {
        return 180;
    }

    @Override
	public int cL() {
        return 180;
    }

    @Override
	public void collide(Entity entity) {}

    @Override
	public float aA() {
        return 0.0F;
    }

    @Override
	@Nullable
    protected MinecraftKey J() {
        return LootTables.z;
    }

    static class c extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        public c(EntityShulker entityshulker) {
            super(entityshulker, EntityLiving.class, 10, true, false, new Predicate() {
                public boolean a(@Nullable EntityLiving entityliving) {
                    return entityliving instanceof IMonster;
                }

                @Override
				public boolean apply(@Nullable Object object) {
                    return this.a((EntityLiving) object);
                }
            });
        }

        @Override
		public boolean a() {
            return this.e.aQ() == null ? false : super.a();
        }

        @Override
		protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.e).dh();

            return enumdirection.k() == EnumDirection.EnumAxis.X ? this.e.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.k() == EnumDirection.EnumAxis.Z ? this.e.getBoundingBox().grow(d0, d0, 4.0D) : this.e.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    class d extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public d(EntityShulker entityshulker) {
            super(entityshulker, EntityHuman.class, true);
        }

        @Override
		public boolean a() {
            return EntityShulker.this.world.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.a();
        }

        @Override
		protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.e).dh();

            return enumdirection.k() == EnumDirection.EnumAxis.X ? this.e.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.k() == EnumDirection.EnumAxis.Z ? this.e.getBoundingBox().grow(d0, d0, 4.0D) : this.e.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    class a extends PathfinderGoal {

        private int b;

        public a() {
            this.a(3);
        }

        @Override
		public boolean a() {
            EntityLiving entityliving = EntityShulker.this.getGoalTarget();

            return entityliving != null && entityliving.isAlive() ? EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL : false;
        }

        @Override
		public void c() {
            this.b = 20;
            EntityShulker.this.a(100);
        }

        @Override
		public void d() {
            EntityShulker.this.a(0);
        }

        @Override
		public void e() {
            if (EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.b;
                EntityLiving entityliving = EntityShulker.this.getGoalTarget();

                EntityShulker.this.getControllerLook().a(entityliving, 180.0F, 180.0F);
                double d0 = EntityShulker.this.h(entityliving);

                if (d0 < 400.0D) {
                    if (this.b <= 0) {
                        this.b = 20 + EntityShulker.this.random.nextInt(10) * 20 / 2;
                        EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(EntityShulker.this.world, EntityShulker.this, entityliving, EntityShulker.this.dh().k());

                        EntityShulker.this.world.addEntity(entityshulkerbullet);
                        EntityShulker.this.a(SoundEffects.fJ, 2.0F, (EntityShulker.this.random.nextFloat() - EntityShulker.this.random.nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    EntityShulker.this.setGoalTarget((EntityLiving) null);
                }

                super.e();
            }
        }
    }

    class e extends PathfinderGoal {

        private int b;

        private e() {}

        @Override
		public boolean a() {
            return EntityShulker.this.getGoalTarget() == null && EntityShulker.this.random.nextInt(40) == 0;
        }

        @Override
		public boolean b() {
            return EntityShulker.this.getGoalTarget() == null && this.b > 0;
        }

        @Override
		public void c() {
            this.b = 20 * (1 + EntityShulker.this.random.nextInt(3));
            EntityShulker.this.a(30);
        }

        @Override
		public void d() {
            if (EntityShulker.this.getGoalTarget() == null) {
                EntityShulker.this.a(0);
            }

        }

        @Override
		public void e() {
            --this.b;
        }

        e(Object object) {
            this();
        }
    }

    class b extends EntityAIBodyControl {

        public b(EntityLiving entityliving) {
            super(entityliving);
        }

        @Override
		public void a() {}
    }
}
